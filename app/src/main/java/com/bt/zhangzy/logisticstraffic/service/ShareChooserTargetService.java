package com.bt.zhangzy.logisticstraffic.service;

/**
 * 直接分享
 * 直接分享是在 APP 内直接弹出一个选择分享到其他应用的中的对象的列表，中间省略了选择需要分享的 APP，选择“联系人”之类的操作。
 * Android 中分享已经做得很好了，这里又更进一步简化了分享操作。如果要让你的 APP 支持被直接分享，需要实现一个 ChooserTargetService，
 * <p/>
 * 并且实现对应的处理分享 Intent 的 Activity。具体使用可以参考这里。
 * http://stackoverflow.com/questions/30518321/on-android-m-how-to-configure-the-direct-share-capabilities-image-text-an/30721038#30721038
 * Created by ZhangZy on 2016-2-1.
 */
//@TargetApi(Build.VERSION_CODES.M)
//public class ShareChooserTargetService extends ChooserTargetService {
//    @Override
//    public List<ChooserTarget> onGetChooserTargets(ComponentName targetActivityName, IntentFilter matchedFilter) {
//        final List<ChooserTarget> targets = new ArrayList<>();
////        for (int i = 0; i < length; i++) {
////            // The title of the target
////            final String title = ...
////            // The icon to represent the target
////            final Icon icon = ...
////            // Ranking score for this target between 0.0f and 1.0f
////            final float score = ...
////            // PendingIntent to fill in and send if the user chooses this target
////            final PendingIntent action = ...
////            targets.add(new ChooserTarget(title, icon, score, action));
////        }
//        return targets;
//    }
//}
