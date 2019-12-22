package com.StaticPH.MicroAud.audioPlayer;

import com.StaticPH.MicroAud.AssortedUtils;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "WeakerAccess", "RedundantSuppression"})
public class PlayerTypeMap {    //TODO: DOCUMENT ME
	private static final Logger loggo = AssortedUtils.getLogger(PlayerTypeMap.class);

	// Need to be a map< playerType, Set<types it can play> >
	// Treat null keys and values already in the map as not present
	// Treat the attempted insertion of null keys and values as an error
	protected static Map<AbstractAudioPlayer, Set<String>> playermap = new HashMap<>();

	public static Map<AbstractAudioPlayer, Set<String>> getPlayermap() { return playermap; }

	//FIXME: I RETURN NULL CONSISTENTLY
	public static Set<String> get(AbstractAudioPlayer player) {
		Objects.requireNonNull(player);
		return playermap.get(player);
	}

//	public static Set<String> get(Class <AbstractAudioPlayer> player) {
////	public static Set<String> get(Class<? extends AbstractAudioPlayer> player) {
//		Objects.requireNonNull(player);
//		return playermap.get(player));
//	}

	public static int size() {return playermap.size();}

	public static void clear() {playermap.clear();}

//	public static boolean isEmpty() { return playermap.isEmpty();}

	public static Set<? extends AbstractAudioPlayer> keySet() { return playermap.keySet();}

	public static Collection<Set<String>> values() {return playermap.values();}

	public static Set<Map.Entry<AbstractAudioPlayer, Set<String>>> entrySet() { return playermap.entrySet();}

	public static boolean equals(Map<? extends AbstractAudioPlayer, Set<String>> m) { return playermap.equals(m);}

	public static boolean containsKey(AbstractAudioPlayer player) {return playermap.containsKey(player);}

	public static boolean containsValue(Set<String> s) {return playermap.containsValue(s);}

	public static boolean containsMapping(AbstractAudioPlayer player, String str) {
		Objects.requireNonNull(player);
//		Objects.requireNonNull(str);
		Set<String> s = playermap.get(player);
		return (s != null && s.contains(str));
//		if (!containsKey(player)){return false;}
//		else if (!containsMappingTo(str) && playermap.get(player) != null){return false;}
	}

	public static boolean containsMappingTo(String str) {
		return playermap.values().stream().anyMatch(set -> set.contains(str));
	}

	public static AbstractAudioPlayer getPlayerWithMappingTo(String str) throws UnsupportedAudioFileException {
		// Treats all values as uppercase for simplicity. Hope this doesnt backfire.
		Vector<AbstractAudioPlayer> players =
			playermap.keySet().stream()
			         .filter(p -> get(p).contains(str.toUpperCase()))
			         .collect(Collectors.toCollection(Vector::new));
		if (players.size() > 1) {
			loggo.warn("Found multiple AudioPlayers capable of handling this type: {}." +
			           "It is recommended that only one type of AudioPlayer map to any given type.", str);
		}
		AbstractAudioPlayer player =
			players.stream().findAny().orElseThrow(
				() -> new UnsupportedAudioFileException(
					"No mapping exists for an AudioPlayer capable of handling type \"" + str +'\"'
				)
			);
		loggo.info("Using AudioPlayer \"" + player.getClass().getSimpleName() + '"');
		return player;
	}

	public static void mergeKeySets(AbstractAudioPlayer player, Set<String> set) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(set);
		playermap.merge(player, set, (set1, set2) -> {
			set1.addAll(set2);
			return set1;
		});
	}

	public static void insert(AbstractAudioPlayer player, Set<String> set) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(set);
		if (playermap.containsKey(player)) {
			mergeKeySets(player, set);
		}
		else {playermap.putIfAbsent(player, set);}
	}

	public static void insert(AbstractAudioPlayer player, String str) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(str);
		Set<String> set = Collections.singleton(str);
		if (playermap.containsKey(player)) {
			mergeKeySets(player, set);
		}
		else {playermap.putIfAbsent(player, set);}
	}

	public static void insert(AbstractAudioPlayer player, String... strings) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(strings);
		Set<String> set = new HashSet<>();
		Collections.addAll(set, strings);
		if (playermap.containsKey(player)) {
			mergeKeySets(player, set);
		}
		else {playermap.putIfAbsent(player, set);}
	}

	//FIXME: I THROW NPE CONSISTENTLY BECAUSE playermap.get IS NOT BEHAVING AS EXPECTED
	public static boolean remove(AbstractAudioPlayer player, String str) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(str);
		Set<String> s = playermap.get(player); //apparently playermap.get is returning null now???
		if (s.size() >= 1) {
			playermap.remove(player);
			return true;
		}
		Set<String> sNew = playermap.get(player).stream().filter(v -> !v.equals(str)).collect(Collectors.toSet());
		playermap.replace(player, s, sNew);
		try {return playermap.get(player).remove(str);}
		catch (NullPointerException e) { return false;}
	}

	/* The sole non-static method in the class */
	@Override
	public String toString() {return playermap.toString();}

}
