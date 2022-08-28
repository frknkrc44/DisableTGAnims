// Copyright (C) 2020-2021 Furkan Karcıoğlu <https://gitlab.com/frknkrc44>
//
// This file is part of DisableTGAnims project,
// and licensed under GNU Affero General Public License v3.
// See the GNU Affero General Public License for more details.
//
// All rights reserved. See COPYING, AUTHORS.
//

package org.blinksd.fucktganims;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SMain implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("PKG loaded for: " + lpparam.packageName);
        try {
            Class<?> imageReceiverClass = XposedHelpers.findClassIfExists("org.telegram.messenger.ImageReceiver", lpparam.classLoader);
            Class<?> animEmojiClass = XposedHelpers.findClassIfExists("org.telegram.ui.Components.AnimatedEmojiDrawable", lpparam.classLoader);
            // Class<?> animatedFDClass = XposedHelpers.findClassIfExists("org.telegram.ui.Components.AnimatedFileDrawable", lpparam.classLoader);
            // Class<?> rlottieDClass = XposedHelpers.findClassIfExists("org.telegram.ui.Components.RLottieDrawable", lpparam.classLoader);
            if (imageReceiverClass == null || animEmojiClass == null /*animatedFDClass == null || rlottieDClass == null*/) {
                XposedBridge.log(lpparam.packageName + " is not Telegram client or code obfuscated!");
                return;
            }

            XposedBridge.hookAllMethods(imageReceiverClass, "setAllowStartAnimation", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.args[0] = false;
                }
            });

            XposedBridge.hookAllMethods(imageReceiverClass, "setAllowStartLottieAnimation", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.args[0] = false;
                }
            });

            XposedBridge.hookAllMethods(imageReceiverClass, "setAutoRepeat", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    param.args[0] = 0;
                }
            });

            // XposedBridge.hookAllMethods(animEmojiClass, "initDocument", XC_MethodReplacement.returnConstant(null));
            XposedBridge.hookAllMethods(animEmojiClass, "invalidate", XC_MethodReplacement.returnConstant(null));
            // XposedBridge.hookAllMethods(animatedFDClass, "start", XC_MethodReplacement.returnConstant(null));
            // XposedBridge.hookAllMethods(rlottieDClass, "start", XC_MethodReplacement.returnConstant(null));
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }
}
