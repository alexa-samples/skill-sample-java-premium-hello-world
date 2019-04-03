package handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.model.IntentRequest;
import util.IspUtil;
import com.amazon.ask.model.Response;
import util.SkillData;

import java.util.Optional;

public class CancelAndStopIntentHandler implements IntentRequestHandler {

    @Override
    public boolean canHandle(HandlerInput handlerInput, IntentRequest intentRequest) {
        return intentRequest.getIntent().getName().equals("AMAZON.StopIntent") ||
                intentRequest.getIntent().getName().equals("AMAZON.CancelIntent");
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput, IntentRequest intentRequest) {
        final String speechText = IspUtil.getRandomObject(SkillData.GOODBYE_STRINGS);
        return handlerInput.getResponseBuilder()
                .withSpeech(speechText)
                .build();
    }

}
