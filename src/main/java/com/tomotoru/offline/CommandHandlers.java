package com.tomotoru.offline;

import java.util.Random;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Full entrance and main command handlers matching tools/server/handlers/auth.py & main.py.
 */
public final class CommandHandlers {
    public static final int RESPONSE_SUCCEED = 1;
    public static final int RESPONSE_TAKEOVER_MISS = -100;

    // Entrance commands
    public static final int ENTRANCE_CMD_AUTH = 0;
    public static final int ENTRANCE_CMD_AGREED_TOS = 1;
    public static final int ENTRANCE_CMD_TAKEOVER_ISSUE = 10;
    public static final int ENTRANCE_CMD_TAKEOVER_USE = 11;
    public static final int ENTRANCE_CMD_DEBUG_NEW_DATA = 900;
    public static final int ENTRANCE_CMD_DEBUG_TEST_DATA = 901;
    public static final int ENTRANCE_CMD_DEBUG_TUTORIALEND_DATA = 902;
    public static final int ENTRANCE_CMD_DEBUG_COMPLETE_DATA = 903;

    // Main commands
    public static final int MAIN_CMD_SAVE_USERDATA = -1;
    public static final int MAIN_CMD_SEND_LOG = 1;
    public static final int MAIN_CMD_CLIENT_REPORT = 2;
    public static final int MAIN_CMD_UPDATE_USERDATA = 10;
    public static final int MAIN_CMD_LOAD_CONF = 100;
    public static final int MAIN_CMD_END_TUTORIAL = 200;
    public static final int MAIN_CMD_TUTORIAL_ADD_HAPINESS = 201;
    public static final int MAIN_CMD_SESSION_CHECK = 300;
    public static final int MAIN_CMD_INPUT_PLAYERINFO = 400;
    public static final int MAIN_CMD_GET_ALL = 900;
    public static final int MAIN_CMD_GET_RM = 901;
    public static final int MAIN_CMD_GET_GM = 902;
    public static final int MAIN_CMD_REGISTER_TOKEN = 903;
    public static final int MAIN_CMD_SELECT_FIRST_COSTUME = 1100;
    public static final int MAIN_CMD_WORK_START = 1101;
    public static final int MAIN_CMD_WORK_UPDATE = 1102;
    public static final int MAIN_CMD_WORK_COMPLETE = 1103;
    public static final int MAIN_CMD_WORK_CANCEL = 1104;
    public static final int MAIN_CMD_WORK_SHORTENING = 1105;
    public static final int MAIN_CMD_LEVEL_UP_COSTUME = 1106;
    public static final int MAIN_CMD_PAT_COSTUME = 1107;
    public static final int MAIN_CMD_RENTAL_COSTUME = 1108;
    public static final int MAIN_CMD_COSTUMEREQUEST_RECIEVE = 1150;
    public static final int MAIN_CMD_COSTUMEREQUEST_TIMEOUT = 1151;
    public static final int MAIN_CMD_COSTUMEREQUEST_FIRST = 1152;
    public static final int MAIN_CMD_PRODUCT_UPDATE = 1250;
    public static final int MAIN_CMD_PRODUCT_START = 1251;
    public static final int MAIN_CMD_PRODUCT_COMPLETE = 1252;
    public static final int MAIN_CMD_PRODUCT_LEVELUP_START = 1253;
    public static final int MAIN_CMD_PRODUCT_LEVELUP_COMPLETE = 1254;
    public static final int MAIN_CMD_PRODUCT_SHORTEN = 1255;
    public static final int MAIN_CMD_PRODUCT_CANCEL = 1256;
    public static final int MAIN_CMD_PRODUCT_DELIVER_COMPLETE = 1257;
    public static final int MAIN_CMD_PRODUCT_FINISH = 1258;
    public static final int MAIN_CMD_PRODUCT_RECIEVE_TIMEOUT = 1259;
    public static final int MAIN_CMD_PRODUCT_TAP = 1260;
    public static final int MAIN_CMD_PRODUCT_REWARDSTATE_CHANGE = 1261;
    public static final int MAIN_CMD_LAYOUT_EDIT = 1300;
    public static final int MAIN_CMD_LAYOUT_REMOVE = 1301;
    public static final int MAIN_CMD_LAYOUT_BUY = 1302;
    public static final int MAIN_CMD_LAYOUT_BUY_NO_DELIVER = 1303;
    public static final int MAIN_CMD_USE_ONCE_ITEM = 1400;
    public static final int MAIN_CMD_USE_BOOST_ITEM = 1401;
    public static final int MAIN_CMD_COMUNICATION_TAP = 1500;
    public static final int MAIN_CMD_ADD_POOL_HAPINESS = 1501;
    public static final int MAIN_CMD_PANELMISSION_SELECT = 1600;
    public static final int MAIN_CMD_PANELMISSION_SEND_PROGRESS = 1601;
    public static final int MAIN_CMD_PANELMISSION_SETPIECE = 1605;
    public static final int MAIN_CMD_PANELMISSION_SHOWEDEVENT = 1606;
    public static final int MAIN_CMD_PANELMISSION_RECEIVE_REWARD = 1607;
    public static final int MAIN_CMD_SELECT_REPLYKEY = 1650;
    public static final int MAIN_CMD_RECIEVE_AD_VIDEO_REWARD = 1900;
    public static final int MAIN_CMD_ADD_ROOM_CHARA = 2000;
    public static final int MAIN_CMD_REMOVE_ROOM_CHARA = 2001;
    public static final int MAIN_CMD_CHANGE_ROOM_CHARA = 2002;
    public static final int MAIN_CMD_SELL_FURNITURE = 3000;
    public static final int MAIN_CMD_SELL_ROOM = 3001;
    public static final int MAIN_CMD_SHOP_BUY = 3002;
    public static final int MAIN_CMD_UNLOCK_ROOM = 3003;
    public static final int MAIN_CMD_EXECUTE_GACHA = 3100;
    public static final int MAIN_CMD_EXECUTE_LIMIT_GACHA = 3101;
    public static final int MAIN_CMD_EXECUTE_STEP_GACHA = 3102;
    public static final int MAIN_CMD_ROOM_BUY = 4000;
    public static final int MAIN_CMD_ROOM_UNLOCK_SLOT = 4100;
    public static final int MAIN_CMD_ROOM_SLOT_EDIT = 4101;
    public static final int MAIN_CMD_ROOM_SLOT_SELECT = 4102;
    public static final int MAIN_CMD_CHARGE_RM = 5000;
    public static final int MAIN_CMD_RESTORE_RM = 5001;
    public static final int MAIN_CMD_RECEIVE_NEW_MAIL = 6000;
    public static final int MAIN_CMD_SET_READ_MAIL_FLAG = 6001;
    public static final int MAIN_CMD_RECEIVE_MAIL_ITEM = 6002;
    public static final int MAIN_CMD_RECEIVE_ALL_MAIL_ITEM = 6003;
    public static final int MAIN_CMD_DELETE_MAIL = 6004;
    public static final int MAIN_CMD_DELETE_MAILS = 6005;
    public static final int MAIN_CMD_CHARA_MOTION_REWARD = 7000;
    public static final int MAIN_CMD_UPDATE_CAPTURE_TIME = 8000;
    public static final int MAIN_CMD_EXECUTE_CAPTURE_HAPINESS = 8001;
    public static final int MAIN_CMD_RECEIVE_SHARE_BONUS = 8002;

    private static final String TAKEOVER_STORE_KEY = "_takeover_codes";
    private static final int[] FIRST_COSTUME_IDS = {109001, 108002, 111101, 101102, 108003, 106103};
    private static final Random RNG = new Random();

    private CommandHandlers() {}

    private static JSONObject makeResponse(int cmd, int ret) throws JSONException {
        JSONObject resp = new JSONObject();
        resp.put("cmd", cmd);
        resp.put("ret", ret);
        resp.put("server_time", UserData.gameServerTime());
        return resp;
    }

    private static JSONObject makeResponse(int cmd) throws JSONException {
        return makeResponse(cmd, RESPONSE_SUCCEED);
    }

    private static int allocUid(JSONObject ud) throws JSONException {
        int next = ud.optInt("next_uid", 100);
        ud.put("next_uid", next + 1);
        return next;
    }

    private static void addGm(JSONObject ud, int delta) throws JSONException {
        ud.put("gmoney", ud.optInt("gmoney", 5000) + delta);
        JSONArray roomEdits = ud.optJSONArray("room_edits");
        if (roomEdits != null && roomEdits.length() > 0) {
            JSONObject re0 = roomEdits.optJSONObject(0);
            if (re0 != null) {
                re0.put("gmoney", re0.optInt("gmoney", 5000) + delta);
            }
        }
    }

    private static void ensureCostume(JSONObject ud, int costumeId) throws JSONException {
        JSONArray costumes = ud.optJSONArray("costumes");
        if (costumes == null) {
            costumes = new JSONArray();
            ud.put("costumes", costumes);
        }
        for (int i = 0; i < costumes.length(); i++) {
            JSONObject c = costumes.optJSONObject(i);
            if (c != null && c.optInt("id", -1) == costumeId) {
                return;
            }
        }
        costumes.put(UserData.makeCostume(costumeId));
    }

    private static int extractIntParam(Object params, String key, int defaultVal) {
        if (params instanceof Number) {
            return ((Number) params).intValue();
        }
        if (params instanceof JSONObject && key != null) {
            JSONObject obj = (JSONObject) params;
            if (obj.has(key) && !obj.isNull(key)) {
                return obj.optInt(key, defaultVal);
            }
        }
        return defaultVal;
    }

    private static int extractFurnitureId(Object params) {
        if (params instanceof JSONObject) {
            JSONObject obj = (JSONObject) params;
            if (obj.has("furniture_id")) {
                return obj.optInt("furniture_id", 8401);
            }
            if (obj.has("shop_id")) {
                return obj.optInt("shop_id", 8401);
            }
        } else if (params instanceof JSONArray) {
            JSONArray arr = (JSONArray) params;
            if (arr.length() > 0) {
                return arr.optInt(0, 8401);
            }
        }
        return 8401;
    }

    private static String rollGachaReward(JSONObject ud) throws JSONException {
        int cosId = FIRST_COSTUME_IDS[RNG.nextInt(FIRST_COSTUME_IDS.length)];
        ensureCostume(ud, cosId);
        int rewardUid = allocUid(ud);
        JSONArray arr = new JSONArray();
        arr.put(rewardUid);
        arr.put(1);
        arr.put(cosId);
        arr.put(1);
        arr.put(0);
        arr.put(1);
        return arr.toString();
    }

    public static JSONObject handleEntrance(JsonStore store, JSONObject params, boolean debug)
            throws JSONException {
        int cmd = params.optInt("cmd", 0);
        String uidIn = params.optString("uuid", "");
        if (uidIn == null || uidIn.length() == 0) {
            uidIn = UUID.randomUUID().toString();
        }

        switch (cmd) {
            case ENTRANCE_CMD_AUTH: {
                String sessionId = UUID.randomUUID().toString();
                UserData.LoadResult res = UserData.loadOrCreateUser(store, uidIn, debug);
                res.userData.put("last_login", UserData.gameServerTime());
                store.save(uidIn, res.userData);

                JSONObject resp = makeResponse(ENTRANCE_CMD_AUTH);
                resp.put("uuid", uidIn);
                resp.put("sessionId", sessionId);
                resp.put("addr", JSONObject.NULL);
                resp.put("first_login", res.isNew);
                resp.put("userdata", UserData.serializeUserData(res.userData));
                return resp;
            }
            case ENTRANCE_CMD_AGREED_TOS:
                return makeResponse(ENTRANCE_CMD_AGREED_TOS);

            case ENTRANCE_CMD_TAKEOVER_ISSUE: {
                String password = params.optString("password", "");
                String code = "TK" + UUID.randomUUID().toString().replace("-", "")
                        .substring(0, 8).toUpperCase();
                JSONObject codes = store.load(TAKEOVER_STORE_KEY);
                if (codes == null) {
                    codes = new JSONObject();
                }
                JSONObject entry = new JSONObject();
                entry.put("uuid", uidIn);
                entry.put("password", password);
                entry.put("created_at", UserData.gameServerTime());
                codes.put(code, entry);
                store.save(TAKEOVER_STORE_KEY, codes);

                JSONObject resp = makeResponse(ENTRANCE_CMD_TAKEOVER_ISSUE);
                resp.put("takeover_code", code);
                return resp;
            }
            case ENTRANCE_CMD_TAKEOVER_USE: {
                String code = params.optString("takeover_code", "");
                String password = params.optString("password", "");
                JSONObject codes = store.load(TAKEOVER_STORE_KEY);
                JSONObject entry = (codes != null) ? codes.optJSONObject(code) : null;
                if (entry == null || !password.equals(entry.optString("password", null))) {
                    return makeResponse(ENTRANCE_CMD_TAKEOVER_USE, RESPONSE_TAKEOVER_MISS);
                }
                JSONObject resp = makeResponse(ENTRANCE_CMD_TAKEOVER_USE);
                resp.put("uuid", entry.optString("uuid", uidIn));
                return resp;
            }
            case ENTRANCE_CMD_DEBUG_NEW_DATA:
            case ENTRANCE_CMD_DEBUG_TEST_DATA:
            case ENTRANCE_CMD_DEBUG_TUTORIALEND_DATA:
            case ENTRANCE_CMD_DEBUG_COMPLETE_DATA: {
                String sessionId = UUID.randomUUID().toString();
                JSONObject ud = UserData.createDefaultUser(uidIn, false);
                if (cmd == ENTRANCE_CMD_DEBUG_TUTORIALEND_DATA
                        || cmd == ENTRANCE_CMD_DEBUG_COMPLETE_DATA) {
                    JSONArray tuts = new JSONArray();
                    for (int i = 1; i <= 5; i++) {
                        tuts.put(i);
                    }
                    ud.put("end_tutorial_ids", tuts);
                }
                if (cmd == ENTRANCE_CMD_DEBUG_COMPLETE_DATA) {
                    ud.put("gmoney", 999999);
                    ud.getJSONArray("room_edits").getJSONObject(0).put("gmoney", 999999);
                    ud.put("freerm", 9999);
                }
                store.save(uidIn, ud);

                JSONObject resp = makeResponse(cmd);
                resp.put("uuid", uidIn);
                resp.put("sessionId", sessionId);
                resp.put("addr", JSONObject.NULL);
                resp.put("first_login", true);
                resp.put("userdata", UserData.serializeUserData(ud));
                return resp;
            }
            default: {
                JSONObject resp = makeResponse(cmd);
                resp.put("uuid", uidIn);
                resp.put("sessionId", UUID.randomUUID().toString());
                return resp;
            }
        }
    }

    public static JSONObject handleMain(
            JsonStore store, int cmd, Object params, String uid, boolean debug) throws JSONException {
        switch (cmd) {
            case MAIN_CMD_SESSION_CHECK:
                return makeResponse(MAIN_CMD_SESSION_CHECK);

            case MAIN_CMD_GET_ALL: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                JSONObject resp = makeResponse(MAIN_CMD_GET_ALL);
                resp.put("userdata", UserData.serializeUserData(ud));
                return resp;
            }
            case MAIN_CMD_UPDATE_USERDATA: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                JSONObject resp = makeResponse(MAIN_CMD_UPDATE_USERDATA);
                resp.put("userdata", UserData.serializeUserData(ud));
                return resp;
            }
            case MAIN_CMD_SAVE_USERDATA:
                return makeResponse(MAIN_CMD_SAVE_USERDATA);

            case MAIN_CMD_GET_RM: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                int freerm = ud.optInt("freerm", 100);
                int rmoney = ud.optInt("rmoney", 0);
                JSONObject resp = makeResponse(MAIN_CMD_GET_RM);
                resp.put("free_rm", freerm);
                resp.put("freerm", freerm);
                resp.put("rmoney", rmoney);
                return resp;
            }
            case MAIN_CMD_GET_GM: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                int gm = ud.optInt("gmoney", 5000);
                JSONObject resp = makeResponse(MAIN_CMD_GET_GM);
                resp.put("gm", gm);
                resp.put("gmoney", gm);
                return resp;
            }
            case MAIN_CMD_LOAD_CONF: {
                JSONObject conf = new JSONObject();
                conf.put("GameRule", GameSettings.getGameRule().toString());
                conf.put("ConditionSetting", "[]");
                JSONObject resp = makeResponse(MAIN_CMD_LOAD_CONF);
                resp.put("conf", conf);
                return resp;
            }
            case MAIN_CMD_SEND_LOG:
                return makeResponse(MAIN_CMD_SEND_LOG);

            case MAIN_CMD_CLIENT_REPORT:
            case MAIN_CMD_REGISTER_TOKEN:
                return makeResponse(MAIN_CMD_CLIENT_REPORT);

            case MAIN_CMD_END_TUTORIAL: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                JSONArray existing = ud.optJSONArray("end_tutorial_ids");
                if (existing == null) {
                    existing = new JSONArray();
                    ud.put("end_tutorial_ids", existing);
                }
                JSONArray toAdd = new JSONArray();
                if (params instanceof JSONObject) {
                    JSONObject obj = (JSONObject) params;
                    JSONArray ids = obj.optJSONArray("ids");
                    if (ids != null) {
                        toAdd = ids;
                    } else if (obj.has("id")) {
                        toAdd.put(obj.optInt("id"));
                    }
                } else if (params instanceof Number) {
                    toAdd.put(((Number) params).intValue());
                } else if (params instanceof JSONArray) {
                    toAdd = (JSONArray) params;
                }
                boolean changed = false;
                for (int i = 0; i < toAdd.length(); i++) {
                    int tid = toAdd.optInt(i);
                    boolean found = false;
                    for (int j = 0; j < existing.length(); j++) {
                        if (existing.optInt(j) == tid) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        existing.put(tid);
                        changed = true;
                    }
                }
                if (changed) {
                    store.save(uid, ud);
                }
                JSONObject resp = makeResponse(MAIN_CMD_END_TUTORIAL);
                resp.put("new_panel_mission_ids", "[]");
                resp.put("set_panel_mission_id", -1);
                return resp;
            }
            case MAIN_CMD_TUTORIAL_ADD_HAPINESS: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                addGm(ud, 500);
                store.save(uid, ud);
                return makeResponse(MAIN_CMD_TUTORIAL_ADD_HAPINESS);
            }
            case MAIN_CMD_INPUT_PLAYERINFO: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                if (params instanceof JSONObject) {
                    JSONObject obj = (JSONObject) params;
                    if (obj.has("name")) {
                        ud.put("name", obj.optString("name", "Player"));
                    }
                    if (obj.has("sex")) {
                        ud.put("sex", obj.optInt("sex", 0));
                    }
                    store.save(uid, ud);
                }
                return makeResponse(MAIN_CMD_INPUT_PLAYERINFO);
            }
            case MAIN_CMD_SELECT_FIRST_COSTUME: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                int idx = extractIntParam(params, "idx", 0);
                int cosId = (idx >= 0 && idx < FIRST_COSTUME_IDS.length)
                        ? FIRST_COSTUME_IDS[idx]
                        : FIRST_COSTUME_IDS[0];
                ensureCostume(ud, cosId);
                store.save(uid, ud);
                JSONObject resp = makeResponse(MAIN_CMD_SELECT_FIRST_COSTUME);
                resp.put("costume_id", cosId);
                return resp;
            }
            case MAIN_CMD_WORK_START: {
                JSONObject resp = makeResponse(MAIN_CMD_WORK_START);
                resp.put("work_id", extractIntParam(params, "work_id", 1));
                resp.put("start_time", UserData.gameServerTime());
                return resp;
            }
            case MAIN_CMD_WORK_UPDATE: {
                JSONObject resp = makeResponse(MAIN_CMD_WORK_UPDATE);
                resp.put("remaining_time", 0);
                return resp;
            }
            case MAIN_CMD_WORK_COMPLETE: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                addGm(ud, 200);
                store.save(uid, ud);
                JSONObject resp = makeResponse(MAIN_CMD_WORK_COMPLETE);
                resp.put("work_id", extractIntParam(params, "work_id", 1));
                resp.put("reward_index", 0);
                resp.put("reward0", JSONObject.NULL);
                return resp;
            }
            case MAIN_CMD_WORK_CANCEL: {
                JSONObject resp = makeResponse(MAIN_CMD_WORK_CANCEL);
                resp.put("work_id", extractIntParam(params, "work_id", 1));
                return resp;
            }
            case MAIN_CMD_WORK_SHORTENING: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                JSONObject resp = makeResponse(MAIN_CMD_WORK_SHORTENING);
                resp.put("work_id", extractIntParam(params, "work_id", 1));
                resp.put("rmoney", ud.optInt("rmoney", 0));
                resp.put("freerm", ud.optInt("freerm", 100));
                return resp;
            }
            case MAIN_CMD_LEVEL_UP_COSTUME: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                int cosId = extractIntParam(params, "costume_id", -1);
                int newLevel = 2;
                if (cosId >= 0) {
                    JSONArray costumes = ud.optJSONArray("costumes");
                    if (costumes != null) {
                        for (int i = 0; i < costumes.length(); i++) {
                            JSONObject c = costumes.optJSONObject(i);
                            if (c != null && c.optInt("id", -1) == cosId) {
                                newLevel = c.optInt("level", 1) + 1;
                                c.put("level", newLevel);
                                break;
                            }
                        }
                    }
                    store.save(uid, ud);
                }
                JSONObject resp = makeResponse(MAIN_CMD_LEVEL_UP_COSTUME);
                resp.put("costume_level", newLevel);
                return resp;
            }
            case MAIN_CMD_PAT_COSTUME: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                long now = UserData.gameServerTime();
                int cosId = extractIntParam(params, "costume_id", -1);
                if (cosId >= 0) {
                    JSONArray costumes = ud.optJSONArray("costumes");
                    if (costumes != null) {
                        for (int i = 0; i < costumes.length(); i++) {
                            JSONObject c = costumes.optJSONObject(i);
                            if (c != null && c.optInt("id", -1) == cosId) {
                                c.put("pat_time", now);
                                break;
                            }
                        }
                    }
                    store.save(uid, ud);
                }
                return makeResponse(MAIN_CMD_PAT_COSTUME);
            }
            case MAIN_CMD_RENTAL_COSTUME: {
                long now = UserData.gameServerTime();
                JSONObject resp = makeResponse(MAIN_CMD_RENTAL_COSTUME);
                resp.put("time_limit", now + 3600L);
                resp.put("server_time", now);
                return resp;
            }
            case MAIN_CMD_COSTUMEREQUEST_RECIEVE:
            case MAIN_CMD_COSTUMEREQUEST_TIMEOUT:
            case MAIN_CMD_COSTUMEREQUEST_FIRST:
                return makeResponse(cmd);

            case MAIN_CMD_PRODUCT_UPDATE: {
                JSONObject resp = makeResponse(MAIN_CMD_PRODUCT_UPDATE);
                resp.put("production_list", new JSONArray());
                return resp;
            }
            case MAIN_CMD_PRODUCT_START:
            case MAIN_CMD_PRODUCT_LEVELUP_START: {
                JSONObject resp = makeResponse(cmd);
                resp.put("start_time", UserData.gameServerTime());
                resp.put("product_time", 60);
                resp.put("reward_states", "[0]");
                return resp;
            }
            case MAIN_CMD_PRODUCT_COMPLETE: {
                JSONObject resp = makeResponse(MAIN_CMD_PRODUCT_COMPLETE);
                resp.put("reward", new JSONArray());
                return resp;
            }
            case MAIN_CMD_PRODUCT_LEVELUP_COMPLETE:
            case MAIN_CMD_PRODUCT_SHORTEN:
            case MAIN_CMD_PRODUCT_DELIVER_COMPLETE:
            case MAIN_CMD_PRODUCT_FINISH:
                return makeResponse(cmd);

            case MAIN_CMD_PRODUCT_CANCEL:
            case MAIN_CMD_PRODUCT_RECIEVE_TIMEOUT: {
                JSONObject resp = makeResponse(cmd);
                resp.put("uid", extractIntParam(params, "uid", 0));
                return resp;
            }
            case MAIN_CMD_PRODUCT_TAP: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                addGm(ud, 10);
                store.save(uid, ud);
                JSONObject resp = makeResponse(MAIN_CMD_PRODUCT_TAP);
                resp.put("add_gm", 10);
                resp.put("bonus_hapiness", 0);
                resp.put("bonus_recover_time", -1);
                return resp;
            }
            case MAIN_CMD_PRODUCT_REWARDSTATE_CHANGE: {
                JSONObject resp = makeResponse(MAIN_CMD_PRODUCT_REWARDSTATE_CHANGE);
                resp.put("finished_uids", JSONObject.NULL);
                return resp;
            }
            case MAIN_CMD_LAYOUT_EDIT:
            case MAIN_CMD_LAYOUT_REMOVE:
                return makeResponse(cmd);

            case MAIN_CMD_LAYOUT_BUY:
            case MAIN_CMD_LAYOUT_BUY_NO_DELIVER: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                long now = UserData.gameServerTime();
                int buyUid = allocUid(ud);
                int furnId = extractFurnitureId(params);
                JSONArray furns = ud.optJSONArray("furnitures");
                if (furns == null) {
                    furns = new JSONArray();
                    ud.put("furnitures", furns);
                }
                JSONObject f = new JSONObject();
                f.put("furniture_id", furnId);
                f.put("uid", buyUid);
                f.put("exp", 0);
                f.put("level", 1);
                f.put("current_view_index", 0);
                f.put("product_type", 0);
                f.put("room_id", 1);
                furns.put(f);
                store.save(uid, ud);

                JSONObject resp = makeResponse(cmd);
                resp.put("buy_uid", buyUid);
                if (cmd == MAIN_CMD_LAYOUT_BUY) {
                    resp.put("deliver_start_time", now);
                    resp.put("product_time", 180);
                }
                return resp;
            }
            case MAIN_CMD_USE_ONCE_ITEM:
            case MAIN_CMD_USE_BOOST_ITEM:
                return makeResponse(cmd);

            case MAIN_CMD_COMUNICATION_TAP: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                addGm(ud, 20);
                store.save(uid, ud);
                JSONObject resp = makeResponse(MAIN_CMD_COMUNICATION_TAP);
                resp.put("add_gm", 20);
                resp.put("bonus_hapiness", 0);
                resp.put("bonus_recover_time", -1);
                return resp;
            }
            case MAIN_CMD_ADD_POOL_HAPINESS: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                addGm(ud, 50);
                store.save(uid, ud);
                return makeResponse(MAIN_CMD_ADD_POOL_HAPINESS);
            }
            case MAIN_CMD_PANELMISSION_SELECT:
            case MAIN_CMD_PANELMISSION_SEND_PROGRESS:
            case MAIN_CMD_PANELMISSION_SETPIECE:
                return makeResponse(cmd);

            case MAIN_CMD_PANELMISSION_SHOWEDEVENT: {
                JSONObject resp = makeResponse(MAIN_CMD_PANELMISSION_SHOWEDEVENT);
                resp.put("clear_date", UserData.gameServerTime());
                return resp;
            }
            case MAIN_CMD_PANELMISSION_RECEIVE_REWARD: {
                JSONObject resp = makeResponse(MAIN_CMD_PANELMISSION_RECEIVE_REWARD);
                resp.put("new_panel_mission_ids", "[]");
                resp.put("set_panel_mission_id", -1);
                return resp;
            }
            case MAIN_CMD_SELECT_REPLYKEY: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                if (params instanceof JSONObject) {
                    JSONObject obj = (JSONObject) params;
                    JSONObject replies = ud.optJSONObject("reply_records");
                    if (replies == null) {
                        replies = new JSONObject();
                        ud.put("reply_records", replies);
                    }
                    JSONArray keys = obj.optJSONArray("keys");
                    JSONArray vals = obj.optJSONArray("values");
                    if (keys != null && vals != null) {
                        int n = Math.min(keys.length(), vals.length());
                        for (int i = 0; i < n; i++) {
                            replies.put(String.valueOf(keys.get(i)), String.valueOf(vals.get(i)));
                        }
                        store.save(uid, ud);
                    } else if (obj.has("key")) {
                        replies.put(obj.optString("key", ""), obj.optString("val", ""));
                        store.save(uid, ud);
                    }
                }
                return makeResponse(MAIN_CMD_SELECT_REPLYKEY);
            }
            case MAIN_CMD_RECIEVE_AD_VIDEO_REWARD: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                long now = UserData.gameServerTime();
                ud.put("last_ad_video_watch_time", now);
                ud.put("ad_video_watch_count", ud.optInt("ad_video_watch_count", 0) + 1);
                addGm(ud, 100);
                store.save(uid, ud);
                JSONObject resp = makeResponse(MAIN_CMD_RECIEVE_AD_VIDEO_REWARD);
                resp.put("last_time", now);
                return resp;
            }
            case MAIN_CMD_ADD_ROOM_CHARA: {
                JSONObject resp = makeResponse(MAIN_CMD_ADD_ROOM_CHARA);
                resp.put("recover_health", 0);
                return resp;
            }
            case MAIN_CMD_REMOVE_ROOM_CHARA: {
                JSONObject resp = makeResponse(MAIN_CMD_REMOVE_ROOM_CHARA);
                resp.put("closet_time", UserData.gameServerTime());
                return resp;
            }
            case MAIN_CMD_CHANGE_ROOM_CHARA: {
                JSONObject resp = makeResponse(MAIN_CMD_CHANGE_ROOM_CHARA);
                resp.put("closet_time", UserData.gameServerTime());
                resp.put("recover_health", "[0,0]");
                return resp;
            }
            case MAIN_CMD_SELL_FURNITURE: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                int targetUid = extractIntParam(params, "uid", -1);
                if (targetUid >= 0) {
                    JSONArray oldArr = ud.optJSONArray("furnitures");
                    JSONArray newArr = new JSONArray();
                    if (oldArr != null) {
                        for (int i = 0; i < oldArr.length(); i++) {
                            JSONObject f = oldArr.optJSONObject(i);
                            if (f != null && f.optInt("uid", -1) != targetUid) {
                                newArr.put(f);
                            }
                        }
                    }
                    ud.put("furnitures", newArr);
                    addGm(ud, 100);
                    store.save(uid, ud);
                }
                return makeResponse(MAIN_CMD_SELL_FURNITURE);
            }
            case MAIN_CMD_SELL_ROOM:
                return makeResponse(MAIN_CMD_SELL_ROOM);

            case MAIN_CMD_SHOP_BUY: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                int rewardUid = allocUid(ud);
                if (params instanceof JSONObject) {
                    JSONObject obj = (JSONObject) params;
                    Object sidObj = obj.has("shop_id") ? obj.opt("shop_id") : obj.opt("id");
                    if (sidObj != null && sidObj != JSONObject.NULL) {
                        JSONObject records = ud.optJSONObject("purchase_records");
                        if (records == null) {
                            records = new JSONObject();
                            ud.put("purchase_records", records);
                        }
                        String key = String.valueOf(sidObj);
                        records.put(key, records.optInt(key, 0) + 1);
                        store.save(uid, ud);
                    }
                }
                JSONArray r0 = new JSONArray();
                r0.put(rewardUid);
                r0.put(1);
                r0.put(109001);
                r0.put(1);
                r0.put(0);
                JSONObject resp = makeResponse(MAIN_CMD_SHOP_BUY);
                resp.put("reward0", r0.toString());
                resp.put("reward1", JSONObject.NULL);
                return resp;
            }
            case MAIN_CMD_UNLOCK_ROOM: {
                JSONObject resp = makeResponse(MAIN_CMD_UNLOCK_ROOM);
                resp.put("room_ids", "[]");
                resp.put("furniture_layouts", "[]");
                resp.put("new_panel_mission_ids", "[]");
                resp.put("set_panel_mission_id", -1);
                return resp;
            }
            case MAIN_CMD_EXECUTE_GACHA:
            case MAIN_CMD_EXECUTE_LIMIT_GACHA:
            case MAIN_CMD_EXECUTE_STEP_GACHA: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                String rewardJson = rollGachaReward(ud);
                store.save(uid, ud);
                JSONObject resp = makeResponse(cmd);
                if (cmd != MAIN_CMD_EXECUTE_GACHA) {
                    resp.put("st", 0);
                }
                resp.put("reward0", rewardJson);
                resp.put("reward1", JSONObject.NULL);
                return resp;
            }
            case MAIN_CMD_ROOM_BUY: {
                JSONObject resp = makeResponse(MAIN_CMD_ROOM_BUY);
                resp.put("room_uids", new JSONArray());
                return resp;
            }
            case MAIN_CMD_ROOM_UNLOCK_SLOT: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                JSONArray existing = ud.optJSONArray("unlocked_slot_ids");
                if (existing == null) {
                    existing = new JSONArray();
                    ud.put("unlocked_slot_ids", existing);
                }
                JSONArray toAdd = new JSONArray();
                if (params instanceof JSONObject) {
                    JSONObject obj = (JSONObject) params;
                    JSONArray sids = obj.optJSONArray("room_slot_ids");
                    if (sids != null) {
                        toAdd = sids;
                    } else if (obj.has("slot_id")) {
                        toAdd.put(obj.optInt("slot_id"));
                    }
                }
                boolean changed = false;
                for (int i = 0; i < toAdd.length(); i++) {
                    int sid = toAdd.optInt(i);
                    boolean found = false;
                    for (int j = 0; j < existing.length(); j++) {
                        if (existing.optInt(j) == sid) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        existing.put(sid);
                        changed = true;
                    }
                }
                if (changed) {
                    store.save(uid, ud);
                }
                return makeResponse(MAIN_CMD_ROOM_UNLOCK_SLOT);
            }
            case MAIN_CMD_ROOM_SLOT_EDIT:
            case MAIN_CMD_ROOM_SLOT_SELECT:
                return makeResponse(cmd);

            case MAIN_CMD_CHARGE_RM: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                ud.put("freerm", ud.optInt("freerm", 100) + 100);
                store.save(uid, ud);
                JSONObject resp = makeResponse(MAIN_CMD_CHARGE_RM);
                resp.put("add_room", -1);
                resp.put("add_rm", 100);
                resp.put("bonus_rm", 0);
                resp.put("productIds", "");
                return resp;
            }
            case MAIN_CMD_RESTORE_RM: {
                JSONObject resp = makeResponse(MAIN_CMD_RESTORE_RM);
                resp.put("add_rm", 0);
                resp.put("bonus_rm", 0);
                resp.put("productIds", "");
                resp.put("room_ids", JSONObject.NULL);
                return resp;
            }
            case MAIN_CMD_RECEIVE_NEW_MAIL: {
                JSONObject resp = makeResponse(MAIN_CMD_RECEIVE_NEW_MAIL);
                resp.put("mails", new JSONArray());
                return resp;
            }
            case MAIN_CMD_SET_READ_MAIL_FLAG:
            case MAIN_CMD_DELETE_MAIL:
                return makeResponse(MAIN_CMD_SET_READ_MAIL_FLAG);

            case MAIN_CMD_RECEIVE_MAIL_ITEM: {
                JSONObject resp = makeResponse(MAIN_CMD_RECEIVE_MAIL_ITEM);
                resp.put("reward", "[0,0,0,100,0]");
                return resp;
            }
            case MAIN_CMD_RECEIVE_ALL_MAIL_ITEM: {
                JSONObject resp = makeResponse(MAIN_CMD_RECEIVE_ALL_MAIL_ITEM);
                resp.put("rewards", new JSONArray());
                return resp;
            }
            case MAIN_CMD_DELETE_MAILS: {
                JSONObject resp = makeResponse(MAIN_CMD_DELETE_MAILS);
                resp.put("idxes", new JSONArray());
                return resp;
            }
            case MAIN_CMD_CHARA_MOTION_REWARD:
                return makeResponse(MAIN_CMD_CHARA_MOTION_REWARD);

            case MAIN_CMD_UPDATE_CAPTURE_TIME: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                long nextTime = UserData.gameServerTime() + 3600L;
                ud.put("capture_hapiness_time", nextTime);
                store.save(uid, ud);
                JSONObject resp = makeResponse(MAIN_CMD_UPDATE_CAPTURE_TIME);
                resp.put("next", nextTime);
                return resp;
            }
            case MAIN_CMD_EXECUTE_CAPTURE_HAPINESS: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                long nextTime = UserData.gameServerTime() + 3600L;
                ud.put("capture_hapiness_time", nextTime);
                addGm(ud, 100);
                store.save(uid, ud);
                JSONObject resp = makeResponse(MAIN_CMD_EXECUTE_CAPTURE_HAPINESS);
                resp.put("reward", 100);
                resp.put("next", nextTime);
                return resp;
            }
            case MAIN_CMD_RECEIVE_SHARE_BONUS: {
                JSONObject ud = UserData.loadOrCreateUser(store, uid, debug).userData;
                addGm(ud, 100);
                store.save(uid, ud);
                return makeResponse(MAIN_CMD_RECEIVE_SHARE_BONUS);
            }
            default:
                return makeResponse(cmd);
        }
    }
}
