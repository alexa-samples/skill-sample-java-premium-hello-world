package handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.ConnectionsResponseHandler;
import util.IspUtil;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.connections.ConnectionsResponse;
import util.SkillData;

import java.util.Optional;

public class CancelfromConnectionsHandler implements ConnectionsResponseHandler {

    @Override
    public boolean canHandle(HandlerInput input, ConnectionsResponse connectionsResponse) {
        final String name = input.getRequestEnvelopeJson().get("request").get("name").asText();
        return name.equalsIgnoreCase("Cancel");
    }

    @Override
    public Optional<Response> handle(HandlerInput input, ConnectionsResponse connectionsResponse) {
        final String speechText = String.format("You will find details for contacting customer support in the card. %s. ", IspUtil.getRandomObject(SkillData.GOODBYE_STRINGS));
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .build();
    }
}
