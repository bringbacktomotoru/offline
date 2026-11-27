package com.tomotoru.offline;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Player UserData model and exact ServerDataLoader flat-array serializer.
 * Mirrors tools/server/persistence/user_data.py field-by-field.
 */
public final class UserData {
    public static final long EPOCH_201511 = 1446336000L;
    public static final int[] ALL_TUTORIAL_IDS = {
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 1001, 1002, 1003
    };
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private UserData() {}

    public static long gameServerTime() {
        long t = (System.currentTimeMillis() / 1000L) - EPOCH_201511;
        return t > 0 ? t : 0;
    }

    public static int makeFriendCode(String uid) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(uid.getBytes(UTF8));
            long val = ((digest[0] & 0xFFL) << 24)
                    | ((digest[1] & 0xFFL) << 16)
                    | ((digest[2] & 0xFFL) << 8)
                    | (digest[3] & 0xFFL);
            return (int) ((val % 900000000L) + 100000000L);
        } catch (Exception e) {
            return 100000001;
        }
    }

    private static JSONObject makeFurniture(int furnitureId, int uid, int roomId) throws JSONException {
        JSONObject f = new JSONObject();
        f.put("furniture_id", furnitureId);
        f.put("uid", uid);
        f.put("exp", 0);
        f.put("level", 1);
        f.put("current_view_index", 0);
        f.put("product_type", 0);
        f.put("room_id", roomId);
        return f;
    }

    public static JSONObject makeCostume(int costumeId) throws JSONException {
        JSONObject c = new JSONObject();
        c.put("id", costumeId);
        c.put("exp", 0);
        c.put("level", 1);
        c.put("health", 100);
        c.put("pat_time", 0);
        c.put("pat_power", 3);
        c.put("pat_mood", 0);
        c.put("closet_time", -1);
        c.put("motions", new JSONArray());
        return c;
    }

    private static JSONObject makeMissionItem(int id) throws JSONException {
        JSONObject m = new JSONObject();
        m.put("id", id);
        m.put("progress", 0);
        m.put("cleared", false);
        m.put("set_piece", false);
        return m;
    }

    public static JSONObject createDefaultUser(String uid, boolean debug) throws JSONException {
        long now = gameServerTime();
        int startGmoney = debug ? 50000 : 5000;
        int startFreerm = debug ? 1000 : 100;

        JSONArray endTutorialIds = new JSONArray();
        if (debug) {
            for (int tid : ALL_TUTORIAL_IDS) {
                endTutorialIds.put(tid);
            }
        }

        JSONObject ud = new JSONObject();
        ud.put("version", 3);
        ud.put("uuid", uid);
        ud.put("friend_code", makeFriendCode(uid));
        ud.put("name", "Player");
        ud.put("sex", 0);
        ud.put("rank", 1);
        ud.put("rankexp", 0);
        ud.put("gmoney", startGmoney);
        ud.put("rmoney", 0);
        ud.put("freerm", startFreerm);
        ud.put("capture_hapiness_time", 0);
        ud.put("next_uid", 100);

        JSONArray furnitures = new JSONArray();
        furnitures.put(makeFurniture(8401, 1, 1));
        furnitures.put(makeFurniture(1703, 2, 1));
        ud.put("furnitures", furnitures);

        JSONArray roomSlots = new JSONArray();
        JSONObject slot0 = new JSONObject();
        slot0.put("room_uid", 1);
        JSONArray reside = new JSONArray();
        reside.put(109001);
        slot0.put("reside_charas", reside);
        roomSlots.put(slot0);
        ud.put("room_slots", roomSlots);

        JSONArray unlockedSlots = new JSONArray();
        unlockedSlots.put(0);
        ud.put("unlocked_slot_ids", unlockedSlots);

        JSONArray roomEdits = new JSONArray();
        JSONObject re0 = new JSONObject();
        re0.put("uid", 1);
        re0.put("room_id", 1);
        re0.put("layouts", new JSONArray());
        re0.put("panel_mission_id", 101);
        re0.put("gmoney", startGmoney);
        roomEdits.put(re0);
        ud.put("room_edits", roomEdits);

        ud.put("room_layout", new JSONArray());

        JSONArray costumes = new JSONArray();
        costumes.put(makeCostume(109001));
        ud.put("costumes", costumes);

        ud.put("work_rewards", new JSONArray());
        ud.put("items", new JSONObject());
        ud.put("purchase_records", new JSONObject());
        ud.put("gacha_data", new JSONArray());
        ud.put("end_tutorial_ids", endTutorialIds);
        ud.put("costume_requests", new JSONArray());
        ud.put("mails", new JSONArray());

        JSONArray characters = new JSONArray();
        JSONObject ch0 = new JSONObject();
        ch0.put("id", 1);
        ch0.put("received_motion_reward_index", -1);
        JSONArray chMotions = new JSONArray();
        chMotions.put(5);
        chMotions.put(9);
        chMotions.put(10);
        ch0.put("motions", chMotions);
        ch0.put("rentals", new JSONObject());
        characters.put(ch0);
        ud.put("characters", characters);

        ud.put("bonus_hapiness_num", 5);
        ud.put("next_bonus_hapiness_recover_time", -1);
        ud.put("last_ad_video_watch_time", 0);
        ud.put("ad_video_watch_count", 0);

        JSONArray panelMissions = new JSONArray();
        JSONObject pm0 = new JSONObject();
        pm0.put("id", 101);
        JSONArray missions = new JSONArray();
        missions.put(makeMissionItem(10));
        missions.put(makeMissionItem(20));
        missions.put(makeMissionItem(30));
        missions.put(makeMissionItem(40));
        pm0.put("missions", missions);
        pm0.put("all_cleared", false);
        pm0.put("clear_date", -1);
        pm0.put("received_reward", false);
        pm0.put("prev_mid_list", new JSONArray());
        panelMissions.put(pm0);
        ud.put("panel_missions", panelMissions);

        ud.put("reply_records", new JSONObject());
        ud.put("created_at", now);
        ud.put("last_login", now);
        return ud;
    }

    public static final class LoadResult {
        public final JSONObject userData;
        public final boolean isNew;

        public LoadResult(JSONObject userData, boolean isNew) {
            this.userData = userData;
            this.isNew = isNew;
        }
    }

    public static LoadResult loadOrCreateUser(JsonStore store, String uid, boolean debug)
            throws JSONException {
        JSONObject ud = store.load(uid);
        if (ud == null || !ud.has("friend_code") || ud.optInt("version", 0) < 3) {
            ud = createDefaultUser(uid, debug);
            store.save(uid, ud);
            return new LoadResult(ud, true);
        }
        return new LoadResult(ud, false);
    }

    public static JSONArray serializeUserData(JSONObject ud) throws JSONException {
        JSONArray out = new JSONArray();
        out.put(ud.optInt("friend_code", 100000001));
        out.put(ud.optString("name", "Player"));
        out.put(ud.optInt("sex", 0));
        out.put(ud.optInt("rank", 1));
        out.put(ud.optInt("rankexp", 0));
        out.put(ud.optInt("gmoney", 5000));
        out.put(ud.optInt("rmoney", 0));
        out.put(ud.optInt("freerm", 100));
        out.put(ud.optLong("capture_hapiness_time", 0L));

        // 1. FurnitureData list
        JSONArray furnitures = ud.optJSONArray("furnitures");
        int fLen = furnitures != null ? furnitures.length() : 0;
        out.put(fLen);
        for (int i = 0; i < fLen; i++) {
            JSONObject f = furnitures.getJSONObject(i);
            out.put(f.optInt("furniture_id", 8401));
            out.put(f.optInt("uid", 1));
            out.put(f.optInt("exp", 0));
            out.put(f.optInt("level", 1));
            out.put(f.optInt("current_view_index", 0));
            out.put(f.optInt("product_type", 0));
            int roomId = f.has("room_id") ? f.optInt("room_id", 1) : f.optInt("room_idx", 1);
            out.put(roomId);
        }

        // 2. RoomSlotData list
        JSONArray roomSlots = ud.optJSONArray("room_slots");
        int sLen = roomSlots != null ? roomSlots.length() : 0;
        out.put(sLen);
        for (int i = 0; i < sLen; i++) {
            JSONObject slot = roomSlots.getJSONObject(i);
            out.put(slot.optInt("room_uid", 1));
            JSONArray reside = slot.optJSONArray("reside_charas");
            int rLen = reside != null ? reside.length() : 0;
            out.put(rLen);
            for (int j = 0; j < rLen; j++) {
                out.put(reside.optInt(j));
            }
        }

        // 3. Unlocked slot IDs
        JSONArray unlocked = ud.optJSONArray("unlocked_slot_ids");
        int uLen = unlocked != null ? unlocked.length() : 0;
        out.put(uLen);
        for (int i = 0; i < uLen; i++) {
            out.put(unlocked.optInt(i));
        }

        // 4. RoomEditData list
        JSONArray roomEdits = ud.optJSONArray("room_edits");
        int reLen = roomEdits != null ? roomEdits.length() : 0;
        out.put(reLen);
        for (int i = 0; i < reLen; i++) {
            JSONObject reItem = roomEdits.getJSONObject(i);
            out.put(reItem.optInt("uid", 1));
            out.put(reItem.optInt("room_id", 1));
            JSONArray layouts = reItem.optJSONArray("layouts");
            int lLen = layouts != null ? layouts.length() : 0;
            out.put(lLen);
            for (int j = 0; j < lLen; j++) {
                JSONArray lay = layouts.optJSONArray(j);
                if (lay != null) {
                    for (int k = 0; k < lay.length(); k++) {
                        out.put(lay.get(k));
                    }
                }
            }
            out.put(reItem.optInt("panel_mission_id", -1));
            out.put(reItem.optInt("gmoney", 5000));
        }

        // 5. room_layout 2D array
        JSONArray roomLayout = ud.optJSONArray("room_layout");
        int rlLen = roomLayout != null ? roomLayout.length() : 0;
        out.put(rlLen);
        for (int i = 0; i < rlLen; i++) {
            JSONArray row = roomLayout.optJSONArray(i);
            int rowLen = row != null ? row.length() : 0;
            out.put(rowLen);
            for (int j = 0; j < rowLen; j++) {
                out.put(row.optInt(j));
            }
        }

        // 6. CostumeData list
        JSONArray costumes = ud.optJSONArray("costumes");
        int cLen = costumes != null ? costumes.length() : 0;
        out.put(cLen);
        for (int i = 0; i < cLen; i++) {
            JSONObject c = costumes.getJSONObject(i);
            out.put(c.optInt("id", 109001));
            out.put(c.optInt("exp", 0));
            out.put(c.optInt("level", 1));
            out.put(c.optInt("health", 100));
            out.put(c.optLong("pat_time", 0L));
            out.put(c.optInt("pat_power", 3));
            out.put(c.optInt("pat_mood", 0));
            out.put(c.optLong("closet_time", -1L));
            JSONArray motions = c.optJSONArray("motions");
            int mLen = motions != null ? motions.length() : 0;
            out.put(mLen);
            for (int j = 0; j < mLen; j++) {
                out.put(motions.optInt(j));
            }
            out.put(0); // has_work_data = 0
        }

        // 7. WorkRewardData list
        out.put(0);

        // 8. Items map
        JSONObject items = ud.optJSONObject("items");
        int itemCount = items != null ? items.length() : 0;
        out.put(itemCount);
        if (items != null) {
            Iterator<String> it = items.keys();
            while (it.hasNext()) {
                String k = it.next();
                out.put(Integer.parseInt(k));
                out.put(items.optInt(k, 0));
            }
        }

        // 9. Purchase records map
        JSONObject purchases = ud.optJSONObject("purchase_records");
        int prCount = purchases != null ? purchases.length() : 0;
        out.put(prCount);
        if (purchases != null) {
            Iterator<String> it = purchases.keys();
            while (it.hasNext()) {
                String k = it.next();
                out.put(Integer.parseInt(k));
                out.put(purchases.optInt(k, 0));
            }
        }

        // 10. GachaData list
        out.put(0);

        // 11. End tutorial IDs
        JSONArray endTuts = ud.optJSONArray("end_tutorial_ids");
        int tLen = endTuts != null ? endTuts.length() : 0;
        out.put(tLen);
        for (int i = 0; i < tLen; i++) {
            out.put(endTuts.optInt(i));
        }

        // 12. CostumeRequestData list
        out.put(0);

        // 13. MailData list
        out.put(0);

        // 14. CharacterData list
        JSONArray characters = ud.optJSONArray("characters");
        int chLen = characters != null ? characters.length() : 0;
        out.put(chLen);
        for (int i = 0; i < chLen; i++) {
            JSONObject ch = characters.getJSONObject(i);
            out.put(ch.optInt("id", 1));
            JSONArray motions = ch.optJSONArray("motions");
            if (motions == null) {
                out.put(3);
                out.put(5);
                out.put(9);
                out.put(10);
            } else {
                out.put(motions.length());
                for (int j = 0; j < motions.length(); j++) {
                    out.put(motions.optInt(j));
                }
            }
            out.put(ch.optInt("received_motion_reward_index", -1));
            JSONObject rentals = ch.optJSONObject("rentals");
            int rCount = rentals != null ? rentals.length() : 0;
            out.put(rCount);
            if (rentals != null) {
                Iterator<String> it = rentals.keys();
                while (it.hasNext()) {
                    String rk = it.next();
                    JSONObject rv = rentals.optJSONObject(rk);
                    out.put(Integer.parseInt(rk));
                    out.put(rv != null ? rv.optInt("id", 0) : 0);
                    out.put(rv != null ? rv.optLong("time_limit", 0L) : 0L);
                }
            }
        }

        // 15. Bonus happiness & ad video stats
        out.put(ud.optInt("bonus_hapiness_num", 5));
        out.put(ud.optLong("next_bonus_hapiness_recover_time", -1L));
        out.put(ud.optLong("last_ad_video_watch_time", 0L));
        out.put(ud.optInt("ad_video_watch_count", 0));

        // 16. PanelMissionData list
        JSONArray panelMissions = ud.optJSONArray("panel_missions");
        int pmLen = panelMissions != null ? panelMissions.length() : 0;
        out.put(pmLen);
        for (int i = 0; i < pmLen; i++) {
            JSONObject pm = panelMissions.getJSONObject(i);
            out.put(pm.optInt("id", 101));
            JSONArray missions = pm.optJSONArray("missions");
            int mLen = missions != null ? missions.length() : 0;
            out.put(mLen);
            for (int j = 0; j < mLen; j++) {
                JSONObject m = missions.getJSONObject(j);
                out.put(m.optInt("id", 0));
                out.put(m.optInt("progress", 0));
                out.put(m.optBoolean("cleared", false) ? 1 : 0);
                out.put(m.optBoolean("set_piece", false) ? 1 : 0);
            }
            out.put(pm.optBoolean("all_cleared", false) ? 1 : 0);
            out.put(pm.optLong("clear_date", -1L));
            out.put(pm.optBoolean("received_reward", false) ? 1 : 0);
            JSONArray prevMids = pm.optJSONArray("prev_mid_list");
            int pLen = prevMids != null ? prevMids.length() : 0;
            out.put(pLen);
            for (int j = 0; j < pLen; j++) {
                out.put(prevMids.optInt(j));
            }
        }

        // 17. Reply records map
        JSONObject replies = ud.optJSONObject("reply_records");
        int repCount = replies != null ? replies.length() : 0;
        out.put(repCount);
        if (replies != null) {
            Iterator<String> it = replies.keys();
            while (it.hasNext()) {
                String k = it.next();
                out.put(k);
                out.put(replies.optString(k, ""));
            }
        }

        return out;
    }
}
