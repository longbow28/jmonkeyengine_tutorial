package org.tutorial.sample;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.anim.tween.action.BlendAction;
import com.jme3.anim.tween.action.BlendSpace;
import com.jme3.anim.tween.action.LinearBlendSpace;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class HelloAnimation extends SimpleApplication {

    private AnimComposer control;
    private Action advance;

    private Node player;

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.LightGray);
        initKeys();
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
        rootNode.addLight(dl);
        player = (Node) assetManager.loadModel("assets/models/Oto.j3o");

        player.setLocalScale(0.5f);
        rootNode.attachChild(player);
        control = player.getControl(AnimComposer.class);

        // 当切换为0秒动画时，会立即切换，无过渡
        // 将stand的时间设置为0.5秒,这样在stand和walk之间切换时会有一个过渡
        Action stand = control.action("stand");
        stand.setLength(0.5f);

        // 设置混合动画
        // 设置一个线性混合空间，范围是0到0.5，0表示全部第一个动画，0.5表示全部第二个动画
        BlendSpace quickBlend = new LinearBlendSpace(0f, 0.5f);
        // 设置混合动画的权重，0.1表示第一个动画80%，第二个动画20%
        quickBlend.setValue(0.2f);
        // 由于“Dodge”下半身无动作，因此和“Walk”混合后，下半身的步幅会变小
        // 由于“Walk”时长比“Dodge”长，因此“Dodge”会慢放拉长至“Walk”时长
        BlendAction smallWalk = control.actionBlended("smallWalk", quickBlend, "Dodge", "Walk");

        Action dodge = control.action("Dodge");
        // 设置dodge的长度为2秒
        // 若动作被用于合成序列动画，则不要修改Length，会造成一些意料之外的问题
        // 设置播放时长，若短于原时长则截取播放，若长于原时长则暂停在最后一帧
        // dodge.setLength(2);
        // 设置dodge的速度为0.1倍，通过setCurrentAction("Dodge")有效，负数会倒放
        // 在拼接为序列动画时，该属性无效
        dodge.setSpeed(0.1);

        // 创建一个慢放动作，播放时长为2秒
        Tween slowDodge = Tweens.stretch(2, dodge);
        // 创建一个反向动作
        Tween slowRevertDodge = Tweens.invert(slowDodge);
        // 创建一个回调方法动作
        Tween doneTween = Tweens.callMethod(this, "onAdvanceDone");

        // 创建一个序列动作,按顺序播放dodge, slowRevertDodge, slowDodge, smallWalk, doneTween
        advance = control.actionSequence("advance", dodge, slowRevertDodge, slowDodge, smallWalk, doneTween);

        
        
        
        
        control.setCurrentAction("stand");

    }

    /**
     * Callback to indicate that the "advance" animation action has completed.
     */
    void onAdvanceDone() {
        /*
         * Play the "stand" animation action.
         */
        control.setCurrentAction("stand");
    }

    private void initKeys() {
        inputManager.addMapping("start", new KeyTrigger(KeyInput.KEY_SPACE));

        ActionListener handler = new ActionListener() {
            @Override
            public void onAction(String name, boolean keyPressed, float tpf) {
                if (keyPressed && control.getCurrentAction() != advance) {
                    control.setCurrentAction("advance");
                }
            }
        };
        inputManager.addListener(handler, "start");
    }
}
