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
        let text = command.argument(at: 0) as! String
        let url = command.argument(at: 1)
        
        // setting text
        composer.setText(text);
        
        // init returning message to clients
        var returnMessage:[String: Any] = [:]
        
        // set image
        if let ur = url {
            let targetURL = URL(string:ur as! String)
            let imageView = UIImageView()
            imageView.contentMode = UIViewContentMode.scaleAspectFit;
            
            // gettig image from URL, so this action is async
            imageView.af_setImage(withURL: targetURL!) { response in
                if let error = response.result.error {
                    returnMessage["status"] = "failed"
                    returnMessage["title"] = "failed getting image from url"
                    returnMessage["description"] = error.localizedDescription
                    let result = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: returnMessage)
                    self.commandDelegate.send(result, callbackId: command.callbackId)
                }
                else {
                    composer.setImage(response.result.value)
                    composer.show(from: self.viewController!) { result in
                        
                        if (result == .done) {
                            returnMessage["status"] = "success"
                            returnMessage["title"] = "send composing tweet with image"
                        } else {
                            returnMessage["status"] = "cancel"
                            returnMessage["title"] = "cancel composing tweet"
                        }
                        
                        let result = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: returnMessage)
                        self.commandDelegate.send(result, callbackId: command.callbackId)
                    }
                }
            }
        }
        else {
            composer.show(from: self.viewController!) { result in
                
                if (result == .done) {
                    returnMessage["status"] = "success"
                    returnMessage["title"] = "send composing tweet "
                } else {
                    returnMessage["status"] = "cancel"
                    returnMessage["title"] = "cancel composing tweet"
                }
                
                let result = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: returnMessage)
                self.commandDelegate.send(result, callbackId: command.callbackId)
            }
        }
    }
}
