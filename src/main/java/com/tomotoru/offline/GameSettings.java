package com.tomotoru.offline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * GameRuleSetting configuration matching tools/server/data/game_settings.py (23.sk.sq).
 */
public final class GameSettings {
    private GameSettings() {}

    private static JSONArray intArray(int... vals) {
        JSONArray arr = new JSONArray();
        for (int v : vals) {
            arr.put(v);
        }
        return arr;
    }

    private static JSONObject motionReward(int needMotion, int num) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("need_motion", needMotion);
        obj.put("num", num);
        return obj;
    }

    private static JSONObject roomMotion(int rank, int motionId) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("rank", rank);
        obj.put("motion_id", motionId);
        return obj;
    }

    public static JSONObject getGameRule() throws JSONException {
        JSONObject rule = new JSONObject();
        rule.put("initial_gm", 5000);
        rule.put("initial_rm", 100);
        rule.put("initial_tap_gm", 0);
        rule.put("shorten_product_per_sec", 180);
        rule.put("shorten_work_per_sec", 180);
        rule.put("first_costume_ids", intArray(109001, 108002, 111101, 101102, 108003, 106103));
        rule.put("default_chara_actions", intArray(10, 20));
        rule.put("costume_request_intervals", intArray(5, 5, 5, 10, 10, 10, 10, 10, 15, 15, 15));
        rule.put("furniture_use_favorite_rates", intArray(20, 10, 0, -10, -20));
        rule.put("bonus_hapiness_rate", 50);
        rule.put("furniture_bonus_hapiness_rate", 100);
        rule.put("levelup_bonus_recovery_num", 5);
        rule.put("chara_sentence_special_rate", 30);
        rule.put("enable_push_hour", intArray(7, 22));
        rule.put("push_costume_request_intervals", intArray(180));
        rule.put("push_language", 18);
        rule.put("furniture_lvmax_rm", 50);
        rule.put("motion_drop_favorite_rates", intArray(20, 10, 0, -10, -20));

        JSONArray motionRewards = new JSONArray();
        int[] needMotions = {1, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
        for (int nm : needMotions) {
            motionRewards.put(motionReward(nm, 50));
        }
        rule.put("motion_rewards", motionRewards);

        rule.put("capture_time", 60);
        rule.put("next_capture_time", 3000);
        rule.put("capture_hapinnes_rate", 100);
        rule.put("pop_capture_time", intArray(0, 60, 180, 300, 480, 600, 900));
        rule.put("ad_video_rewards", intArray(100, 100, 100, 100, 100));
        rule.put("disable_tutorial", true);

        JSONArray roomMotions = new JSONArray();
        int[][] rmPairs = {
            {1, 16}, {1, 1001}, {3, 1}, {5, 2}, {7, 3}, {10, 4}, {13, 5},
            {16, 6}, {19, 7}, {20, 1002}, {22, 8}, {25, 9}, {28, 10}, {30, 1003}
        };
        for (int[] p : rmPairs) {
            roomMotions.put(roomMotion(p[0], p[1]));
        }
        rule.put("room_motions", roomMotions);
        rule.put("initial_motions", intArray(5, 9, 10));
        return rule;
    }
}
