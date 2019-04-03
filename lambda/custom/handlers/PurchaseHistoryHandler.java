package handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.request.RequestHelper;
import util.IspUtil;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.services.monetization.InSkillProduct;
import com.amazon.ask.model.services.monetization.InSkillProductsResponse;
import com.amazon.ask.model.services.monetization.MonetizationServiceClient;
import util.SkillData;

import java.util.List;
import java.util.Optional;

public class PurchaseHistoryHandler implements IntentRequestHandler {

    @Override
    public boolean canHandle(HandlerInput handlerInput, IntentRequest intentRequest) {
        return intentRequest.getIntent().getName().equals("PurchaseHistoryIntent");
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput, IntentRequest intentRequest) {
        final RequestHelper requestHelper = RequestHelper.forHandlerInput(handlerInput);
        final String locale = requestHelper.getLocale();
        final MonetizationServiceClient client = handlerInput.getServiceClientFactory().getMonetizationService();
        final InSkillProductsResponse response = client.getInSkillProducts(locale, null, null, null, null, null);
        final List<InSkillProduct> inSkillProducts = response.getInSkillProducts();
        final List<String> entitledProducts = IspUtil.getAllEntitledProducts(inSkillProducts);
        if (entitledProducts.size() > 0) {
            final String speechText = String.format("You have bought the following items: %s . %s",
                    IspUtil.getSpeakableListOfProducts(entitledProducts), IspUtil.getRandomObject(SkillData.YES_NO_STRINGS));
            final String repromptText = String.format("You asked me for a what you've bought, here's a list: %s",
                    IspUtil.getSpeakableListOfProducts(entitledProducts));
            return handlerInput.getResponseBuilder()
                    .withSpeech(speechText)
                    .withReprompt(repromptText)
                    .build();
        }
        final String speechText = "You haven't purchased anything yet. Would you like a standard greeting or premium greeting";
        final String repromptText = String.format("You asked me for a what you've bought, but you haven't purchased anything yet. You can say - what can I buy, or say yes to get another greeting. %s",
                IspUtil.getRandomObject(SkillData.YES_NO_STRINGS));
        return handlerInput.getResponseBuilder()
                .withSpeech(speechText)
                .withReprompt(repromptText)
                .build();
    }
}
