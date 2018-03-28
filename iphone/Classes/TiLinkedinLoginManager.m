/**
 * ti.linkedin
 *
 * Created by Andrea Vitale
 * Copyright (c) 2018 EveryUP SRL. All rights reserved.
 */

#import "TiLinkedinLoginManager.h"
#import "TiUtils.h"
#import <linkedin-sdk/LISDK.h>

@implementation TiLinkedinLoginManager

+ (id)sharedInstance {
    static TiLinkedinLoginManager* sharedInstance = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        sharedInstance = [[self alloc] init];
    });
    
    return sharedInstance;
}

- (TiLinkedinLoginManager*)init {
    _profileFields = [NSArray arrayWithObjects:
                      @"id", @"first-name", @"last-name",
                      @"email-address", @"formatted-name", @"headline", @"location",
                      @"summary", @"picture-url", @"picture-urls::(original)", nil];
    
    return self;
}

- (void)authorize:(NSArray *)permissions successBlock:(void(^)(NSDictionary* payload))successHandler errorBlock:(void(^)(NSDictionary* payload))errorHandler {
    NSString* fieldsToRequire = [NSString stringWithFormat:@"(%@)", [_profileFields componentsJoinedByString:@","]];
    NSString* requestURI = [NSString stringWithFormat:@"https://api.linkedin.com/v1/people/~:%@", fieldsToRequire];
    
    [LISDKSessionManager createSessionWithAuth:permissions
                                         state:nil
                        showGoToAppStoreDialog: YES
                                  successBlock:^(NSString *returnState) {
                                      LISDKSession *session = [[LISDKSessionManager sharedInstance] session];

                                      if ([LISDKSessionManager hasValidSession]) {
                                          [[LISDKAPIHelper sharedInstance] getRequest:requestURI
                                                                              success:^(LISDKAPIResponse *response) {
                                                                                  NSDictionary* data = [NSJSONSerialization JSONObjectWithData:[[response data] dataUsingEncoding:NSUTF8StringEncoding] options:kNilOptions error:nil];
                                                                                  NSString* uid = [data objectForKey:@"id"];
                                                                                  
                                                                                  _token = [[session accessToken] accessTokenValue];
                                                                                  
                                                                                  successHandler(@{
                                                                                                   @"success": NUMBOOL(YES),
                                                                                                   @"canceled": NUMBOOL(NO),
                                                                                                   @"data": data,
                                                                                                   @"uid": uid
                                                                                                });
                                                                              }
                                                                                error:^(LISDKAPIError *error) {
                                                                                    errorHandler(@{
                                                                                                   @"success": NUMBOOL(NO),
                                                                                                   @"canceled": NUMBOOL(NO),
                                                                                                   @"code": NUMINTEGER([error code]),
                                                                                                   @"error": [error description]
                                                                                                });
                                                                                }];
                                      }
                                  }
                                    errorBlock:^(NSError *error) {
                                            errorHandler(@{
                                                           @"success": NUMBOOL(NO),
                                                           @"canceled": NUMBOOL([error code] == 3),
                                                           @"code": NUMINTEGER([error code]),
                                                           @"error": [error description]
                                                        });
                                        }];
}

- (NSString*)token {
    return _token;
}

#pragma mark Internals

- (void)log:(NSString *)string forLevel:(NSString *)level {
    NSLog(@"[%@] %@: %@", [level uppercaseString], NSStringFromClass([self class]), string);
}

@end
