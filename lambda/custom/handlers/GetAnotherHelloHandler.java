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

import java.util.List;
import java.util.Optional;

public class GetAnotherHelloHandler implements IntentRequestHandler {

    @Override
    public boolean canHandle(HandlerInput handlerInput, IntentRequest intentRequest) {
        return intentRequest.getIntent().getName().equals("AMAZON.YesIntent");
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput, IntentRequest intentRequest) {
        String preSpeechText = "";
        final RequestHelper requestHelper = RequestHelper.forHandlerInput(handlerInput);
        final String locale = requestHelper.getLocale();
        final MonetizationServiceClient client = handlerInput.getServiceClientFactory().getMonetizationService();
        final InSkillProductsResponse response = client.getInSkillProducts(locale, null, null, null, null, null);
        final List<InSkillProduct> inSkillProducts = response.getInSkillProducts();
        return IspUtil.getResponseBasedOnAccessType(handlerInput, inSkillProducts, preSpeechText);
    }

}
