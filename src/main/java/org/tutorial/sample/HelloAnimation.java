package org.tutorial.sample;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
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
        control.setCurrentAction("stand");

        /*
         * Compose an animation action named "halt"
         * that transitions from "Walk" to "stand" in half a second.
         */
        BlendSpace quickBlend = new LinearBlendSpace(0f, 10f);
        Action halt = control.actionBlended("halt", quickBlend, "stand", "push");
        halt.setLength(10f);

        /*
         * Compose an animation action named "advance"
         * that walks for one cycle, then halts, then invokes onAdvanceDone().
         */
        Action walk = control.action("Walk");
        Tween doneTween = Tweens.callMethod(this, "onAdvanceDone");
        advance = control.actionSequence("advance", walk, halt, doneTween);
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

    /**
     * Map the spacebar to the "Walk" input action, and add a listener to initiate
     * the "advance" animation action each time it's pressed.
     */
    private void initKeys() {
        inputManager.addMapping("Walk", new KeyTrigger(KeyInput.KEY_SPACE));

        ActionListener handler = new ActionListener() {
            @Override
            public void onAction(String name, boolean keyPressed, float tpf) {
                if (keyPressed && control.getCurrentAction() != advance) {
                    control.setCurrentAction("advance");
                }
            }
        };
        inputManager.addListener(handler, "Walk");
    }
}
