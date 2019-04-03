package util;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.connections.SendRequestDirective;
import com.amazon.ask.model.services.monetization.InSkillProduct;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class IspUtil {

    public static Optional<Response> getResponseBasedOnAccessType(HandlerInput input, List<InSkillProduct> products, String preSpeechText) {

        final Optional<InSkillProduct> greetingsPackProduct = getGreetingsPackProduct(products);
        final Optional<InSkillProduct> premiumSubscriptionProduct = getPremiumSubscriptionProduct(products);

        String theGreeting;
        String speechText;
        final String repromptText = getRandomObject(SkillData.YES_NO_STRINGS);

        final String[] specialGreeting = getSpecialHello();
        String greetingLanguage = specialGreeting[0];
        String greetingText = specialGreeting[1];

        String preGreetingSpeechText = String.format("%s Here's your special greeting: ", preSpeechText);
        String postGreetingSpeechText = String.format("That's hello in %s", greetingLanguage);

        if(greetingsPackProduct.isPresent() && premiumSubscriptionProduct.isPresent()) {


            if(isEntitled(premiumSubscriptionProduct.get())) {
                //Customer has bought the Premium Subscription. Switch to Polly Voice, and return special hello
                speechText = String.format("%s %s %s", preGreetingSpeechText, getVoiceTalentToSay(String.format("%s! %s", greetingText, postGreetingSpeechText), greetingLanguage), repromptText);
            } else if(isEntitled(greetingsPackProduct.get())) {
                //Customer has bought the Greetings Pack, but not the Premium Subscription. Return special hello greeting in Alexa voice
                speechText = String.format("%s %s! %s. %s", preGreetingSpeechText, greetingText, postGreetingSpeechText, repromptText);

            } else {
                //Customer has NOT bought neither the Premium Subscription nor the Greetings Pack Product.
                //Determine if upsell should be made. returns true/false
                if(shouldUpsell(input)) {
                    String upsellMessage = String.format("By the way, you can now get greetings in more languages. %s. %s", greetingsPackProduct.get().getSummary(), getRandomObject(SkillData.LEARN_MORE_STRINGS));
                    //Upsell Greetings Pack
                    return input.getResponseBuilder()
                            .addDirective(getUpsellDirective(greetingsPackProduct.get().getProductId(), upsellMessage))
                            .build();
                }
                theGreeting = getRandomObject(SkillData.HELLO_STRINGS);
                speechText = String.format("Here's your simple greeting: %s. %s", theGreeting, repromptText);
            }
        } else {
            speechText = String.format("Sorry, no in-skill product found. Here's your simple greeting: %s. %s", IspUtil.getRandomObject(SkillData.HELLO_STRINGS), repromptText);
        }
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withReprompt(repromptText)
                .build();
    }

    public static Optional<InSkillProduct> getInSkillProduct(List<InSkillProduct> inSkillProducts, String productId) {
        for(InSkillProduct inSkillProduct : inSkillProducts) {
            if(inSkillProduct.getProductId().equalsIgnoreCase(productId)) {
                return Optional.of(inSkillProduct);
            }
        }
        return Optional.empty();
    }

    public static String getRandomObject(String[] strings) {
        final int index = new Random().nextInt(strings.length);
        return strings[index];
    }

    public static boolean isEntitled(InSkillProduct product) {
        return null!=product && product.getEntitled().toString().equalsIgnoreCase("ENTITLED");
    }

    /**
     * Gets a random premium greeting
     * */
    public static String[] getSpecialHello() {
        String [] strings = new String[2];
        Map<String, String> greetingMap = SkillData.PREMIUM_GREETING_MAP;
        Random random = new Random();
        List<String> keys = new ArrayList<>(greetingMap.keySet());
        final String randomKey = keys.get( random.nextInt(keys.size()) );
        final String value = greetingMap.get(randomKey);
        strings[0] = randomKey;
        strings[1] = value;
        return strings;
    }


    public static SendRequestDirective getDirectiveByType(String productId, String type) {
        // Prepare the directive payload
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> inskillProduct = new HashMap<>();
        inskillProduct.put("productId", productId);
        payload.put("InSkillProduct", inskillProduct);

        // Prepare the directive request
        SendRequestDirective directive = SendRequestDirective.builder()
                .withPayload(payload)
                .withName(type)
                .withToken("correlationToken")
                .build();

        return directive;
    }

    /**
     * Gets the upsell SendRequestDirective
     * @param productId
     * @param upsellMessage
     * @return SendRequestDirective
     * */
    private static SendRequestDirective getUpsellDirective(String productId, String upsellMessage) {

        // Prepare the directive payload
        Map<String,Object> payload = new HashMap<>();
        Map<String, Object> inskillProduct = new HashMap<>();
        inskillProduct.put("productId", productId);
        payload.put("upsellMessage", upsellMessage);
        payload.put("InSkillProduct", inskillProduct);

        return SendRequestDirective.builder()
                .withPayload(payload)
                .withName("Upsell")
                .withToken("correlationToken")
                .build();
    }

    /**
     * Gets a list of available products
     * */
    public static List<String> getListOfAvailableProducts(List<InSkillProduct> inSkillProducts) {
        return inSkillProducts.stream()
                .filter(product -> product.getEntitled().toString().equalsIgnoreCase("NOT_ENTITLED")
                        && product.getPurchasable().toString().equalsIgnoreCase("PURCHASABLE"))
                .map(product -> product.getName())
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of entitled products
     * */
    public static List<String> getAllEntitledProducts(List<InSkillProduct> inSkillProducts) {
        return inSkillProducts.stream()
                .filter(product -> product.getEntitled().toString().equalsIgnoreCase("ENTITLED"))
                .map(product -> product.getName())
                .collect(Collectors.toList());
    }

    public static Optional<InSkillProduct> getGreetingsPackProduct(List<InSkillProduct> products) {
        for(InSkillProduct product : products) {
            if(product.getReferenceName().equalsIgnoreCase("Greetings_Pack")) {
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }

    public static Optional<InSkillProduct> getPremiumSubscriptionProduct(List<InSkillProduct> products) {
        for(InSkillProduct product : products) {
            if(product.getReferenceName().equalsIgnoreCase("Premium_Subscription")) {
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }

    public static String getSpeakableListOfProducts(List<String> inSkillProducts) {
        String s = inSkillProducts.toString();
        String productListSpeech = s.substring(1, s.length() - 1).replace(", ", ",");
        return productListSpeech.replaceFirst(",([^,]+)$", " and$1");
    }

    /**
     * Checks if upsell should be made
     */
    public static boolean shouldUpsell(HandlerInput input) {
        if (null == input.getRequestEnvelopeJson().get("request").get("intent")) {
            //If the last intent was Connections.Response, do not upsell
            return false;
        }
        boolean[] options = new boolean[]{true, false};
        Random random = new Random();
        int index = random.nextInt(options.length);
        return options[index];
    }

    private static String getVoiceTalentToSay(String speakOutput, String language) {
        final String personality = getVoicePersonality(language);
        return String.format("<voice name=\"%s\"> %s </voice>", personality, speakOutput);
    }

    private static String getVoicePersonality(String language) {
        final Map<String, String[]> map = SkillData.VOICE_PERSONALITIES;
        return IspUtil.getRandomObject(map.get(language));
    }

    public static String getBuyResponseText(String referenceName, String productName) {
        if (referenceName.equalsIgnoreCase("Greetings_Pack")) {
            return String.format("With the %s, I can now say hello in a variety of languages.", productName);
        } else if (referenceName.equalsIgnoreCase("Premium_Subscription")) {
            return String.format("With the %s, I can now say hello in a variety of languages, in different accents using Amazon Polly.", productName);
        }
        System.out.println("Product Undefined");
        return "Sorry, that's not a valid product";
    }

}
