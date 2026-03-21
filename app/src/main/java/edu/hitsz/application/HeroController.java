package edu.hitsz.application;

import edu.hitsz.aircraft.HeroAircraft;

/**
 * 英雄机控制类
 * Android版本 - 触屏控制
 * 注意：在Android上，触屏事件已通过BaseGame的setOnTouchListener处理
 * 这个类作为备用接口保留，便于后续扩展功能（如震动反馈、按钮控制等）
 *
 * @author hitsz
 */
public class HeroController {
    private Game game;
    private HeroAircraft heroAircraft;

    public HeroController(Game game, HeroAircraft heroAircraft) {
        this.game = game;
        this.heroAircraft = heroAircraft;
        
        // Android上的触屏处理已在BaseGame中通过setOnTouchListener完成
        // 如需扩展功能（如获取按键输入、手柄控制等），可在此添加
    }

    /**
     * 更新英雄机位置（无需手动调用，由触屏监听器自动处理）
     */
    public void updateHeroPosition(int x, int y) {
        if (heroAircraft != null) {
            heroAircraft.setLocation(x, y);
        }
    }
}

