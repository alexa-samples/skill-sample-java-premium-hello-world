package handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.ConnectionsResponseHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.connections.ConnectionsResponse;
import com.amazon.ask.model.services.monetization.InSkillProduct;
import com.amazon.ask.model.services.monetization.InSkillProductsResponse;
import com.amazon.ask.model.services.monetization.MonetizationServiceClient;
import com.amazon.ask.request.RequestHelper;
import util.IspUtil;

import java.util.List;
import java.util.Optional;

public class BuyResponseHandler implements ConnectionsResponseHandler {

    private static final String SUCCESS_CODE = "200";

    @Override
    public boolean canHandle(HandlerInput handlerInput, ConnectionsResponse connectionsResponse) {
        final String name = handlerInput.getRequestEnvelopeJson().get("request").get("name").asText();
        return name.equalsIgnoreCase("Buy") || name.equalsIgnoreCase("Upsell");
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput, ConnectionsResponse connectionsResponse) {
        final RequestHelper requestHelper = RequestHelper.forHandlerInput(handlerInput);
        final String locale = requestHelper.getLocale();
        final MonetizationServiceClient client = handlerInput.getServiceClientFactory().getMonetizationService();
        final String productId = handlerInput.getRequestEnvelopeJson().get("request").get("payload").get("productId").asText();

        final InSkillProductsResponse response = client.getInSkillProducts(locale, null, null, null, null, null);
        final List<InSkillProduct> inSkillProducts = response.getInSkillProducts();

        final Optional<InSkillProduct> inSkillProduct = IspUtil.getInSkillProduct(inSkillProducts, productId);
        final String code = handlerInput.getRequestEnvelopeJson().get("request").get("status").get("code").asText();
        if (inSkillProduct.isPresent() && code.equalsIgnoreCase(SUCCESS_CODE)) {
            String preSpeechText;
            final String purchaseResult = handlerInput.getRequestEnvelopeJson().get("request").get("payload").get("purchaseResult").asText();

            switch (purchaseResult) {
                case "ACCEPTED": {
                    preSpeechText = IspUtil.getBuyResponseText(inSkillProduct.get().getReferenceName(), inSkillProduct.get().getName());
                    break;
                }
                case "DECLINED": {
                    preSpeechText = "No Problem.";
                    break;
                }
                case "ALREADY_PURCHASED": {
                    preSpeechText = IspUtil.getBuyResponseText(inSkillProduct.get().getReferenceName(), inSkillProduct.get().getName());
                    break;
                }
                default:
                    preSpeechText = String.format("Something unexpected happened, but thanks for your interest in the %s.", inSkillProduct.get().getName());
            }
            return IspUtil.getResponseBasedOnAccessType(handlerInput, inSkillProducts, preSpeechText);
        }
        //Something failed
        System.out.println(String.format("Connections.Response indicated failure. error: %s", handlerInput.getRequestEnvelopeJson().get("request").get("status").get("message").toString()));
        return handlerInput.getResponseBuilder()
                .withSpeech("There was an error handling your purchase request. Please try again or contact us for help.")
                .build();


    }

}
