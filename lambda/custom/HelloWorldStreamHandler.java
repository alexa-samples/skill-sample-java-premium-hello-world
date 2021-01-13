import com.amazon.ask.Skill;
import com.amazon.ask.Skills;
import com.amazon.ask.SkillStreamHandler;
import handlers.*;

public class HelloWorldStreamHandler extends SkillStreamHandler {

    private static Skill getSkill() {
        return Skills.standard()
                .addRequestHandlers(
                        new LaunchHandler(),
                        new SimpleHelloIntentHandler(),
                        new GetAnotherHelloHandler(),
                        new NoIntentHandler(),
                        new BuyResponseHandler(),
                        new FallbackIntentHandler(),
                        new WhatCanIBuyIntentHandler(),
                        new BuyGreetingsPackIntentHandler(),
                        new BuyPremiumSubscriptionIntentHandler(),
                        new PurchaseHistoryHandler(),
                        new RefundGreetingsPackIntentHandler(),
                        new CancelPremiumSubscriptionIntentHandler(),
                        new CancelfromConnectionsHandler(),
                        new HelpIntentHandler(),
                        new CancelAndStopIntentHandler(),
                        new SessionEndedHandler())
                .addExceptionHandlers(new GenericExceptionHandler())
                // Add your skill id below
                //.withSkillId("")
                .build();
    }

    public HelloWorldStreamHandler() {
        super(getSkill());
    }

}
