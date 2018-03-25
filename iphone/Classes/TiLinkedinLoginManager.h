/**
 * ti.linkedin
 *
 * Created by Andrea Vitale
 * Copyright (c) 2018 EveryUP SRL. All rights reserved.
 */

#import <Foundation/Foundation.h>

@interface TiLinkedinLoginManager : NSObject

@property(nonatomic, retain) NSArray* profileFields;

- (TiLinkedinLoginManager*)init;

- (void)authorize:(NSArray *)permissions successBlock:(void(^)(NSDictionary* payload))successHandler errorBlock:(void(^)(NSDictionary* payload))errorHandler;

@end
