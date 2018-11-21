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
    return [[Twitter sharedInstance] application:app openURL:url options:options];
}

@end