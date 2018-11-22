//
//  AppDelegate+Sso.m
//  Sso
//
//  Created by shogo on 2018/02/04.
//
//

#import "AppDelegate+TWComposer.h"
#import <TwitterKit/TWTRKit.h>
@implementation AppDelegate (TWComposer)

- (BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary<NSString *, id> *)options
{
    NSRange twitter = [url.absoluteString rangeOfString:@"twitterkit"];
    if (twitter.location != NSNotFound) {
        return [[Twitter sharedInstance] application:app openURL:url options:options];
    }
    else{
        // call super
        return [self application:app openURL:url options:options];
    }
}

@end