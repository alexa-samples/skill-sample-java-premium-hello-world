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

import java.util.List;
import java.util.Optional;

public class WhatCanIBuyIntentHandler implements IntentRequestHandler {

    @Override
    public boolean canHandle(HandlerInput handlerInput, IntentRequest intentRequest) {
        return intentRequest.getIntent().getName().equals("WhatCanIBuyIntent");
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput, IntentRequest intentRequest) {
        final RequestHelper requestHelper = RequestHelper.forHandlerInput(handlerInput);
        final String locale = requestHelper.getLocale();
        final MonetizationServiceClient client = handlerInput.getServiceClientFactory().getMonetizationService();
        final InSkillProductsResponse response = client.getInSkillProducts(locale, null, null, null, null, null);
        final List<InSkillProduct> inSkillProducts = response.getInSkillProducts();
        final List<String> availableProducts = IspUtil.getListOfAvailableProducts(inSkillProducts);
        if (availableProducts.size() > 0) {
            final String speechText = String.format("Products available for purchase at this time are %s. To learn more about a product, say 'Tell me more about' followed by the product name. If you are ready to buy, say, 'Buy' followed by the product name. So what can I help you with?",
                    IspUtil.getSpeakableListOfProducts(availableProducts));
            final String repromptText = "I didn't catch that. What can I help you with?";
            return handlerInput.getResponseBuilder()
                    .withSpeech(speechText)
                    .withReprompt(repromptText)
                    .build();
        }
        final String speechText = "There are no products to offer to you right now. Sorry about that. Would you like a greeting instead?";
        final String repromptText = "I didn't catch that. What can I help you with?";
        return handlerInput.getResponseBuilder()
                .withSpeech(speechText)
                .withReprompt(repromptText)
                .build();
    }

}
