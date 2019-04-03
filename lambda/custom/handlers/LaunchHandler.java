package handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.LaunchRequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;

import java.util.Optional;

public class LaunchHandler implements LaunchRequestHandler {

    @Override
    public boolean canHandle(HandlerInput handlerInput, LaunchRequest launchRequest) {
        return true;
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput, LaunchRequest launchRequest) {
        final String speechText = "Welcome to Premium Hello World. To hear a greeting, you can say hello!";
        return handlerInput.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("Premium Hello World", speechText)
                .withReprompt(speechText)
                .build();
    }

}
