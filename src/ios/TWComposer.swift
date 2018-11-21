import TwitterKit
import AlamofireImage

@objc(TWComposer) class TWComposer : CDVPlugin {
    
    var callbackId:String?
    
    override func pluginInitialize() {
        // setting up twitter instance;
        let consumerKey = self.commandDelegate.settings["twitterconsumerkey"] as? String
        let consumerSecret = self.commandDelegate.settings["twitterconsumersecret"] as? String
        TWTRTwitter.sharedInstance().start(withConsumerKey: consumerKey!, consumerSecret: consumerSecret! );
    }

    func compose(_ command: CDVInvokedUrlCommand) {
        let composer = TWTRComposer()
        let text = command.argument(at: 0)
        let url = command.argument(at: 1)
        
        // set text
        composer.setText(text as! String);
        
        
        // set image
        if let ur = url {
            let targetURL = URL(string:ur as! String)
            let imageView = UIImageView()
            imageView.contentMode = UIViewContentMode.scaleAspectFit;
            imageView.af_setImage(withURL: targetURL!) { response in
                if let error = response.result.error {
                    // TODO: error handling
                    print(error)
                }
                else {
                    composer.setImage(response.result.value)
                    composer.show(from: self.viewController!) { result in
                        if (result == .done) {
                            print("Success dfully composed Tweet")
                        } else {
                            print("Cancelled composing")
                        }
                    }
                }
            }
        }
        else {
            composer.show(from: self.viewController!) { result in
                if (result == .done) {
                    print("Success dfully composed Tweet")
                } else {
                    print("Cancelled composing")
                }
            }
        }
    }
}
