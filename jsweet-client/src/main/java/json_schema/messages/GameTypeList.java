package json_schema.messages;

import static def.jquery.Globals.$;

import java.util.ArrayList;
import java.util.List;

import json_schema.GameSetup2;
import jsweet.lang.Array;

public class GameTypeList extends Message2 {

	public static final String TYPE = "game-type-list";

	public GameTypeList() {
		super(TYPE);
	}

	public GameTypeList(String type) {
		super(type);
	}

	public List<GameSetup2> getGameTypes() {
		@SuppressWarnings("unchecked")
		Array<GameSetup2> array = (Array<GameSetup2>) $.extend(true, new Array<GameSetup2>(), $get("game-types"));

		List<GameSetup2> set = new ArrayList<>();
		for (GameSetup2 obj : array) {
			GameSetup2 gameSetup = (GameSetup2) $.extend(false, new GameSetup2(), obj);
			set.add(gameSetup);
		}
		return set;
	}

	public static GameTypeList map(Object obj) {
		return (GameTypeList) $.extend(false, new GameTypeList(), obj);
	}
}