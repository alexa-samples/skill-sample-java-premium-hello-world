package handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

// 2018-July-09: AMAZON.FallackIntent is only currently available in en-US locale.
//              This handler will not be triggered except in that locale, so it can be
//              safely deployed for any locale.
public class FallbackIntentHandler implements IntentRequestHandler {

    @Override
    public boolean canHandle(HandlerInput handlerInput, IntentRequest intentRequest) {
        return intentRequest.getIntent().getName().equals("AMAZON.FallbackIntent");
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput, IntentRequest intentRequest) {
        final String speechText = "Sorry, I don't know that. You can try saying help!";
        return handlerInput.getResponseBuilder()
                .withSpeech(speechText)
                .withReprompt("You can try saying help!")
                .build();
    }

}
