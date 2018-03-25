/**
 * ti.linkedin
 *
 * Created by Andrea Vitale
 * Copyright (c) 2018 EveryUP SRL. All rights reserved.
 */

#import "TiLinkedinModule.h"
#import "TiBase.h"
#import "TiHost.h"
#import "TiUtils.h"
#import "TiApp.h"
#import <linkedin-sdk/LISDK.h>

@implementation TiLinkedinModule

#pragma mark Internal

// This is generated for your module, please do not change it
- (id)moduleGUID {
	return @"42a4497b-4b3c-4050-94c0-0bbca185c745";
}

// This is generated for your module, please do not change it
- (NSString *)moduleId {
	return @"ti.linkedin";
}

#pragma mark Lifecycle

- (void)startup {
	[super startup];

    _permissions = [NSArray arrayWithObjects:LISDK_BASIC_PROFILE_PERMISSION, LISDK_EMAILADDRESS_PERMISSION, nil];
}

- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation {
    if ([LISDKCallbackHandler shouldHandleUrl:url]) {
        return [LISDKCallbackHandler application:application openURL:url sourceApplication:sourceApplication annotation:annotation];
    }

    return YES;
}

- (void)handleOpenURL:(NSNotification *)notification {
    NSDictionary *launchOptions = [[TiApp app] launchOptions];
    NSURL *url = [NSURL URLWithString:[launchOptions objectForKey:@"url"]];
    NSString *sourceApplication = [launchOptions objectForKey:@"source"];

    id annotation = nil;

    if ([TiUtils isIOS9OrGreater]) {
        #ifdef __IPHONE_9_0
            annotation = [launchOptions objectForKey:UIApplicationOpenURLOptionsAnnotationKey];
        #endif
    }

    if ([LISDKCallbackHandler shouldHandleUrl:url]) {
        [LISDKCallbackHandler application:[UIApplication sharedApplication] openURL:url sourceApplication:sourceApplication annotation:annotation];
    }
}

#pragma mark Properties

- (NSNumber *)loggedIn {
    return NUMBOOL([LISDKSessionManager hasValidSession]);
}

- (NSArray<NSString*> *_Nullable) permissions {
    return _permissions;
}

- (void)setPermissions:(NSArray<NSString *> *_Nullable)permissions {    
    _permissions = permissions;
}

#pragma mark Public APIs

- (void)initialize:(__unused id)unused {
    TiThreadPerformOnMainThread(^{
        NSNotificationCenter* notificationCenter = [NSNotificationCenter defaultCenter];

        [notificationCenter addObserver:self
                               selector:@selector(handleOpenURL:)
                                   name:@"TiApplicationLaunchedFromURL"
                                 object:nil];
    }, YES);
}

- (void)authorize:(__unused id)unused {
    ENSURE_UI_THREAD(authorize, unused);
    
    TiLinkedinLoginManager* loginManager = [[TiLinkedinLoginManager alloc] init];
    
    [loginManager authorize:_permissions
               successBlock:^(NSDictionary* payload) {
                   if ([self _hasListeners:@"login"]) {
                       [self fireEvent:@"login" withObject:payload];
                   }
               } errorBlock:^(NSDictionary* payload) {
                   if ([self _hasListeners:@"login"]) {
                       [self fireEvent:@"login" withObject:payload];
                   }
               }];
}

- (void)logout:(__unused id)unused {
    [LISDKSessionManager clearSession];

    if (![LISDKSessionManager hasValidSession] && [self _hasListeners:@"logout"]) {
        [self fireEvent:@"logout" withObject:nil];
    }
}

#pragma mark Internals

- (void)log:(NSString *)string forLevel:(NSString *)level {
    NSLog(@"[%@] %@: %@", [level uppercaseString], NSStringFromClass([self class]), string);
}

#pragma mark Constants

MAKE_SYSTEM_STR(PERMISSION_BASIC_PROFILE, LISDK_BASIC_PROFILE_PERMISSION);
MAKE_SYSTEM_STR(PERMISSION_COMPLETE_PROFILE, LISDK_FULL_PROFILE_PERMISSION);
MAKE_SYSTEM_STR(PERMISSION_EMAIL_ADDRESSES, LISDK_EMAILADDRESS_PERMISSION);
MAKE_SYSTEM_STR(PERMISSION_CONTACT_INFO, LISDK_CONTACT_INFO_PERMISSION);
MAKE_SYSTEM_STR(PERMISSION_SHARE, LISDK_W_SHARE_PERMISSION);
MAKE_SYSTEM_STR(PERMISSION_COMPANY_PERMISSION, LISDK_RW_COMPANY_ADMIN_PERMISSION);

@end
