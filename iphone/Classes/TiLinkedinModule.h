/**
 * ti.linkedin
 *
 * Created by Andrea Vitale
 * Copyright (c) 2018 EveryUP SRL. All rights reserved.
 */

#import "TiModule.h"
#import "TiLinkedinLoginManager.h"

@interface TiLinkedinModule : TiModule {
    NSArray* _permissions;
}

- (void)initialize:(__unused id)unused;
- (void)authorize:(__unused id)unused;
- (void)logout:(__unused id)unused;

- (NSString *_Nullable)accessToken;
- (NSNumber *_Nonnull) loggedIn;
- (NSArray<NSString*> *_Nullable) permissions;

- (void) setPermissions:(NSArray<NSString*> *_Nullable) permissions;

@end
