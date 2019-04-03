package handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.services.monetization.InSkillProduct;
import com.amazon.ask.model.services.monetization.InSkillProductsResponse;
import com.amazon.ask.model.services.monetization.MonetizationServiceClient;
import com.amazon.ask.request.RequestHelper;
import util.IspUtil;
import util.SkillData;

import java.util.List;
import java.util.Optional;

public class BuyGreetingsPackIntentHandler implements IntentRequestHandler {

    @Override
    public boolean canHandle(HandlerInput handlerInput, IntentRequest intentRequest) {
        return intentRequest.getIntent().getName().equals("BuyGreetingsPackIntent");
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput, IntentRequest intentRequest) {
        final RequestHelper requestHelper = RequestHelper.forHandlerInput(handlerInput);
        final String locale = requestHelper.getLocale();
        final MonetizationServiceClient client = handlerInput.getServiceClientFactory().getMonetizationService();
        final InSkillProductsResponse response = client.getInSkillProducts(locale, null, null, null, null, null);
        final List<InSkillProduct> products = response.getInSkillProducts();
        final String repromptText = IspUtil.getRandomObject(SkillData.YES_NO_STRINGS);

        final Optional<InSkillProduct> greetingsPackProduct = IspUtil.getGreetingsPackProduct(products);
        final Optional<InSkillProduct> premiumSubscriptionProduct = IspUtil.getPremiumSubscriptionProduct(products);

        if (greetingsPackProduct.isPresent() && premiumSubscriptionProduct.isPresent()) {
            if (IspUtil.isEntitled(premiumSubscriptionProduct.get())) {
                //Customer has bought the Premium Subscription. They don't need to buy the Greetings Pack.

                final String speechText = String.format("Good News! You're subscribed to the Premium Subscription, which includes all features of the Greetings Pack. %s", repromptText);

                return handlerInput.getResponseBuilder()
                        .withSpeech(speechText)
                        .withReprompt(repromptText)
                        .build();
            }
            //Send Connections.SendRequest Directive back to Alexa to switch to Purchase Flow
            return handlerInput.getResponseBuilder()
                    .addDirective(IspUtil.getDirectiveByType(greetingsPackProduct.get().getProductId(), "Buy"))
                    .build();
        }
        return handlerInput.getResponseBuilder()
                .withSpeech(String.format("Sorry, no in-skill product found. Here's your simple greeting: %s. %s", IspUtil.getRandomObject(SkillData.HELLO_STRINGS), repromptText))
                .withReprompt(repromptText)
                .build();
    }
}
