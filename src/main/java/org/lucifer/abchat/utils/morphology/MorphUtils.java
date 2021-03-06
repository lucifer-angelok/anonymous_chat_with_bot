package org.lucifer.abchat.utils.morphology;

import java.util.ArrayList;
import java.util.List;

public class MorphUtils {

    public static MorphInfo parse(String info) {
        MorphInfo result = new MorphInfo();
        result = parsePartOfSpeech(result, info);
        result = parseGrammems(result, grammems(info));
        result = parseStdForm(result, info);
        return result;
    }

    private static MorphInfo parseStdForm(MorphInfo result, String info) {
        StringBuilder sb = new StringBuilder();
        char[] chars = info.toCharArray();
        int i = 0;
        while (chars[i] != '|') {
            sb.append(chars[i++]);
        }
        result.setStandartForm(sb.toString());
        return result;
    }

    public static List<MorphInfo> parse(List<String> list) {
        List<MorphInfo> ready = new ArrayList<MorphInfo>();
        for (String s : list) {
            ready.add(parse(s));
        }
        return ready;
    }

    private static MorphInfo parseGrammems(MorphInfo obj, String info) {
        obj = grammemsForVerb(obj, info);
        obj = grammemsForNoun(obj, info);
        obj = otherGrammems(obj, info);
        return obj;
    }

    private static MorphInfo otherGrammems(MorphInfo obj, String info) {
        obj = voice(obj, info);
        obj = unchangable(obj, info);
        obj = cut(obj, info);
        obj = compare(obj, info);
        obj = quality(obj, info);
        obj = name(obj, info);
        obj = surname(obj, info);
        obj = patronymic(obj, info);
        obj = locativity(obj, info);
        obj = organization(obj, info);
        obj = qwRel(obj, info);
        obj = dfst(obj, info);
        obj = mistake(obj, info);
        obj = slang(obj, info);
        obj = abbr(obj, info);
        return obj;
    }

    private static MorphInfo abbr(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.ABBR)) {
            obj.setAbbr(true);
        }
        return obj;
    }

    private static MorphInfo slang(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.JARG)) {
            obj.setQwRel(MorphInfo.JARG);
        }
        if (info.contains(MorphInfo.ARH)) {
            obj.setQwRel(MorphInfo.ARH);
        }
        if (info.contains(MorphInfo.PROF)) {
            obj.setQwRel(MorphInfo.PROF);
        }
        return obj;
    }

    private static MorphInfo mistake(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.OPCH)) {
            obj.setMistake(true);
        }
        return obj;
    }

    private static MorphInfo dfst(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.DFST)) {
            obj.setDfst(true);
        }
        return obj;
    }

    private static MorphInfo qwRel(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.VOPR)) {
            obj.setQwRel(MorphInfo.VOPR);
        }
        if (info.contains(MorphInfo.OTNOS)) {
            obj.setQwRel(MorphInfo.OTNOS);
        }
        return obj;
    }

    private static MorphInfo organization(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.ORG)) {
            obj.setOrganization(true);
        }
        return obj;
    }

    private static MorphInfo locativity(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.LOK)) {
            obj.setLocativity(true);
        }
        return obj;
    }

    private static MorphInfo patronymic(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.OTCH)) {
            obj.setPatronymic(true);
        }
        return obj;
    }

    private static MorphInfo surname(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.FAM)) {
            obj.setSurname(true);
        }
        return obj;
    }

    private static MorphInfo name(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.IMYA)) {
            obj.setName(true);
        }
        return obj;
    }

    private static MorphInfo quality(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.KACH)) {
            obj.setQuality(true);
        }
        return obj;
    }

    private static MorphInfo compare(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.SRAVN)) {
            obj.setCompare(true);
        }
        return obj;
    }

    private static MorphInfo cut(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.KR)) {
            obj.setCut(true);
        }
        return obj;
    }

    private static MorphInfo unchangable(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.ZERO)) {
            obj.setUnchangable(true);
        }
        return obj;
    }

    private static MorphInfo voice(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.DST)) {
            obj.setVoice(MorphInfo.DST);
        }
        if (info.contains(MorphInfo.STR)) {
            obj.setVoice(MorphInfo.STR);
        }
        return obj;
    }

    private static MorphInfo grammemsForVerb(MorphInfo obj, String info) {
        obj = perf(obj, info);
        obj = transit(obj, info);
        obj = time(obj, info);
        obj = command(obj, info);
        obj = face(obj, info);
        obj = impersonal(obj, info);
        return obj;
    }

    private static MorphInfo impersonal(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.BEZL)) {
            obj.setImpersonal(true);
        }
        return obj;
    }

    private static MorphInfo face(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.FL)) {
            obj.setFace(MorphInfo.FL);
        }
        if (info.contains(MorphInfo.SL)) {
            obj.setFace(MorphInfo.SL);
        }
        if (info.contains(MorphInfo.TL)) {
            obj.setFace(MorphInfo.TL);
        }
        return obj;
    }

    private static MorphInfo command(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.PVL)) {
            obj.setCommand(true);
        }
        return obj;
    }

    private static MorphInfo time(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.NST)) {
            obj.setTime(MorphInfo.NST);
        }
        if (info.contains(MorphInfo.PRSH)) {
            obj.setTime(MorphInfo.PRSH);
        }
        if (info.contains(MorphInfo.BUD)) {
            obj.setTime(MorphInfo.BUD);
        }
        return obj;
    }

    private static MorphInfo transit(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.PE)) {
            obj.setTransit(MorphInfo.PE);
        }
        if (info.contains(MorphInfo.NP)) {
            obj.setTransit(MorphInfo.NP);
        }
        return obj;
    }

    private static MorphInfo perf(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.SV)) {
            obj.setPerf(MorphInfo.SV);
        }
        if (info.contains(MorphInfo.NS)) {
            obj.setPerf(MorphInfo.NS);
        }
        return obj;
    }

    private static MorphInfo grammemsForNoun(MorphInfo obj, String info) {
        obj = gender(obj, info);
        obj = count(obj, info);
        obj = animate(obj, info);
        obj = nCase(obj, info);
        obj = secondnCase(obj, info);
        return obj;
    }

    private static MorphInfo secondnCase(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.TWO)) {
            obj.setSecond(true);
        }
        return obj;
    }

    private static MorphInfo nCase(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.PR)) {
            obj.setnCase(MorphInfo.PR);
        }
        if (info.contains(MorphInfo.IM)) {
            obj.setnCase(MorphInfo.IM);
        }
        if (info.contains(MorphInfo.RD)) {
            obj.setnCase(MorphInfo.RD);
        }
        if (info.contains(MorphInfo.DT)) {
            obj.setnCase(MorphInfo.DT);
        }
        if (info.contains(MorphInfo.VN)) {
            obj.setnCase(MorphInfo.VN);
        }
        if (info.contains(MorphInfo.TV)) {
            obj.setnCase(MorphInfo.TV);
        }
        if (info.contains(MorphInfo.ZV)) {
            obj.setnCase(MorphInfo.ZV);
        }
        return obj;
    }

    private static MorphInfo animate(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.OD)) {
            obj.setAnimate(true);
        }
        if (info.contains(MorphInfo.NO)) {
            obj.setNotAnimate(true);
        }
        return obj;
    }

    private static MorphInfo count(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.ED)) {
            obj.setCount(MorphInfo.ED);
        }
        if (info.contains(MorphInfo.MN)) {
            obj.setCount(MorphInfo.MN);
        }
        return obj;
    }

    private static MorphInfo gender(MorphInfo obj, String info) {
        if (info.contains(MorphInfo.MR)) {
            obj.setGender(MorphInfo.MR);
        }
        if (info.contains(MorphInfo.JR)) {
            obj.setGender(MorphInfo.JR);
        }
        if (info.contains(MorphInfo.SR)) {
            obj.setGender(MorphInfo.SR);
        }
        return obj;
    }

    private static MorphInfo parsePartOfSpeech(MorphInfo obj, String info) {
        obj = PartOfSpeech(obj, info, MorphInfo.S);
        obj = PartOfSpeech(obj, info, MorphInfo.P);
        obj = PartOfSpeech(obj, info, MorphInfo.MS);
        obj = PartOfSpeech(obj, info, MorphInfo.G);
        obj = PartOfSpeech(obj, info, MorphInfo.INFINITIV);
        obj = PartOfSpeech(obj, info, MorphInfo.MS_PREDK);
        obj = PartOfSpeech(obj, info, MorphInfo.MS_P);
        obj = PartOfSpeech(obj, info, MorphInfo.CHISL);
        obj = PartOfSpeech(obj, info, MorphInfo.CHISL_P);
        obj = PartOfSpeech(obj, info, MorphInfo.N);
        obj = PartOfSpeech(obj, info, MorphInfo.PREDK);
        obj = PartOfSpeech(obj, info, MorphInfo.PREDL);
        obj = PartOfSpeech(obj, info, MorphInfo.SOUZ);
        obj = PartOfSpeech(obj, info, MorphInfo.MEJD);
        obj = PartOfSpeech(obj, info, MorphInfo.CHAST);
        obj = PartOfSpeech(obj, info, MorphInfo.PRICHASTIE);
        obj = PartOfSpeech(obj, info, MorphInfo.DEEPRICHASTIE);
        obj = PartOfSpeech(obj, info, MorphInfo.VVODN);
        obj = PartOfSpeech(obj, info, MorphInfo.KR_PRIL);
        obj = PartOfSpeech(obj, info, MorphInfo.KR_PRICHASTIE);
        return obj;
    }

    private static MorphInfo PartOfSpeech(MorphInfo obj, String info, String pos) {
        if (info.contains(pos)) {
            obj.setPartOfSpeech(pos);
        }
        return obj;
    }

    private static String grammems(String info) {
        String words[] = info.split(" ");
        StringBuilder sb = new StringBuilder();
        boolean afterPOS = false;
        for (String word : words) {
            if (afterPOS) {
                sb.append(word).append(" ");
            }
            if (partOfSpeech(word)) {
                afterPOS = true;
            }
        }
        return sb.toString();
    }

    private static boolean partOfSpeech(String word) {
        return (word != null) && (word.contains(MorphInfo.S.trim()) ||
                word.contains(MorphInfo.P.trim()) ||
                word.contains(MorphInfo.MS.trim()) ||
                word.contains(MorphInfo.G.trim()) ||
                word.contains(MorphInfo.PRICHASTIE.trim()) ||
                word.contains(MorphInfo.DEEPRICHASTIE.trim()) ||
                word.contains(MorphInfo.INFINITIV.trim()) ||
                word.contains(MorphInfo.MS_PREDK.trim()) ||
                word.contains(MorphInfo.MS_P.trim()) ||
                word.contains(MorphInfo.CHISL.trim()) ||
                word.contains(MorphInfo.CHISL_P.trim()) ||
                word.contains(MorphInfo.N.trim()) ||
                word.contains(MorphInfo.PREDK.trim()) ||
                word.contains(MorphInfo.PREDL.trim()) ||
                word.contains(MorphInfo.SOUZ.trim()) ||
                word.contains(MorphInfo.MEJD.trim()) ||
                word.contains(MorphInfo.CHAST.trim()) ||
                word.contains(MorphInfo.VVODN.trim()) ||
                word.contains(MorphInfo.KR_PRIL.trim()) ||
                word.contains(MorphInfo.KR_PRICHASTIE.trim()));
    }
}
