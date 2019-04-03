# Build An Alexa Skill with In-Skill Purchases - Premium Hello World
<img src="https://m.media-amazon.com/images/G/01/mobile-apps/dex/alexa/alexa-skills-kit/tutorials/quiz-game/header._TTH_.png" />

## Setting Up A Lambda Function Using Amazon Web Services

In the [first step of this guide](1-setup-vui-aws-hosted.md), we built the Voice User Interface (VUI) for our Alexa skill.  On this page, we will be creating an AWS Lambda function using [Amazon Web Services](http://aws.amazon.com).  You can [read more about what a Lambda function is](http://aws.amazon.com/lambda), but for the purposes of this guide, what you need to know is that AWS Lambda is where our code lives.  When a user asks Alexa to use our skill, it is our AWS Lambda function that interprets the appropriate interaction, and provides the conversation back to the user.

Refer to [Hosting a Custom Skill as an AWS Lambda Function](https://developer.amazon.com/docs/custom-skills/host-a-custom-skill-as-an-aws-lambda-function.html) reference for a walkthrough on creating a AWS Lambda function with the correct role for your skill. When creating the function, select the “Author from scratch” option, and select the Java 8 runtime.

To build the sample, open a terminal and go to the directory containing pom.xml, and run 'mvn org.apache.maven.plugins:maven-assembly-plugin:2.6:assembly -DdescriptorId=jar-with-dependencies package'. This will generate a zip file named "premium-hello-world-1.0-jar-with-dependencies.jar" in the target directory.
 
Once you've created your AWS Lambda function and configured “Alexa Skills Kit” as a trigger, upload the JAR file produced from above and set the handler to the fully qualified class name of your handler function. Finally, copy the ARN for your AWS Lambda function because you’ll need it when configuring your skill in the [Amazon Developer Portal](https://developer.amazon.com/alexa/console/ask).

[![Next](./next.png)](./3-create-isp.md)