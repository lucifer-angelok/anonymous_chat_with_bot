package org.lucifer.abchat.service.impl;

import org.apache.lucene.morphology.Morphology;
import org.lucifer.abchat.dao.SemanticLinkDao;
import org.lucifer.abchat.domain.Bot;
import org.lucifer.abchat.domain.SemanticLink;
import org.lucifer.abchat.domain.SemanticNetwork;
import org.lucifer.abchat.domain.SemanticNode;
import org.lucifer.abchat.dto.MessageDTO;
import org.lucifer.abchat.utils.morphology.MorphInfo;
import org.lucifer.abchat.utils.morphology.MorphUtils;
import org.lucifer.abchat.utils.morphology.MorphologySingleton;
import org.lucifer.abchat.utils.preprocessor.StringPreProcessor;
import org.lucifer.abchat.utils.semantic.Semantic;
import org.lucifer.abchat.service.ChatService;
import org.lucifer.abchat.service.SemanticNetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional
@Service
public class SemanticNetworkServiceImpl extends BaseServiceImpl<SemanticNetwork> implements SemanticNetworkService {
    private static final String I = "я";
    private static final String YOU = "ты";
    private static final String NOT = "не";
    private static final String IF = "бы";
    private static final String SPACE = " ";


    private static final String HOW = "как";
    private static final String KIND_OF = "какой";

    private static final String WHO = "кто";
    private static final String WHAT = "что";
    private static final String PREDICATE = "делать";
    private static final String PREDICATE_P = "сделать";

    private Morphology morphology = MorphologySingleton.getInstance();

    @Autowired
    private SemanticLinkDao semanticLinkDao;

    @Autowired
    private ChatService chatService;

    public void create(MessageDTO msg) {
        if (msg.getMessage() == null) return;
        if (!isQuestion(msg.getMessage())) {
            Bot bot = chatService.findBotInChat(msg.getChatId());
            SemanticNetwork network = new SemanticNetwork(bot);
            divideIntoNodesAndSave(msg.getMessage(), network);
        }
    }

    public String answer(MessageDTO msg) {
        if (msg.getMessage() == null) return null;
        if (isQuestion(msg.getMessage())) {
            return assembleAnswer(msg);
        } else return null;
    }

    private String assembleAnswer(MessageDTO msg) {
        String[] words = StringPreProcessor.split(msg.getMessage());
        int verbIndex = findVerb(words);
        int subjIndex = findSubject(words, verbIndex);
        int objIndex = findObject(words, subjIndex, verbIndex);
        if (qwContains(msg.getMessage(), KIND_OF)) {
            return features(msg, verbIndex, subjIndex, objIndex);
        }
        if (qwContains(msg.getMessage(), HOW)) {
            if (verbIndex != -1) return images(msg, verbIndex,
                    subjIndex, objIndex);
        }
        if (qwContains(msg.getMessage(), WHO)) {
            String nCase = getNCaseOf(msg.getMessage(), WHO);
            if (nCase != null && nCase.equals(MorphInfo.RD)
                    && objIndex == -1) {
                nCase = MorphInfo.VN;
            }
            if (verbIndex != -1) return subjObjAdd(msg, verbIndex, nCase);
        }
        if ((qwContains(msg.getMessage(), PREDICATE)
                || qwContains(msg.getMessage(), PREDICATE_P))
                && qwContains(msg.getMessage(), WHAT)) {
            if (verbIndex != -1 && subjIndex != -1) return predicateInfo(msg,
                    verbIndex, subjIndex);
        }
        if (qwContains(msg.getMessage(), WHAT)) {
            String nCase = getNCaseOf(msg.getMessage(), WHAT);
            if (subjIndex != -1 && objIndex == -1) nCase = MorphInfo.VN;
            if (subjIndex == -1) nCase = MorphInfo.IM;
            if (verbIndex != -1) return subjObjAdd(msg, verbIndex, nCase);
        }

        return null;
    }

    private String predicateInfo(MessageDTO msg, int verbIndex, int subjIndex) {
        Bot b = chatService.findBotInChat(msg.getChatId());
        StringBuilder ans = new StringBuilder();
        String verb = null, subj = null;
        if (verbIndex != -1) {
            verb = StringPreProcessor.split(msg.getMessage())[verbIndex];
        }
        if (subjIndex != -1) {
            subj = StringPreProcessor.split(msg.getMessage())[subjIndex];
        }
        for (SemanticNetwork network : b.getNetworks()) {
            ans.append(findInNetworkPredicate(network, verb, subj))
                    .append(SPACE);
        }
        return ans.toString().equals("") ? null : ans.toString().trim();
    }

    private String findInNetworkPredicate(SemanticNetwork network, String verb, String subj) {
        StringBuilder sb = new StringBuilder();
        for (SemanticNode node : network.getNodes()) {
            if (node.getPeak()) {
                if (node.getLinkedNode(Semantic.SUBJECT).getValue()
                        .equals(subj)) {
                    sb.append(node.getValue())
                            .append(SPACE);
                    SemanticNode obj = node.getLinkedNode(Semantic.OBJECT);
                    if (obj != null) sb.append(obj.getValue());
                }
            }
        }
        return sb.toString();
    }

    private String subjObjAdd(MessageDTO msg, int verbIndex, String nCase) {
        Bot b = chatService.findBotInChat(msg.getChatId());
        StringBuilder ans = new StringBuilder();
        String verb = null;
        if (verbIndex != -1) {
            verb = StringPreProcessor.split(msg.getMessage())[verbIndex];
        }
        for (SemanticNetwork network : b.getNetworks()) {
            ans.append(findInNetworkSOA(network, verb, nCase))
                    .append(SPACE);
        }
        return ans.toString().equals("") ? null : ans.toString().trim();
    }

    private String findInNetworkSOA(SemanticNetwork network, String verb, String nCase) {
        StringBuilder sb = new StringBuilder();
        String semanticCase = getSemanticCase(nCase);
        for (SemanticNode node : network.getNodes()) {
            if (node.getPeak() && node.getValue().equals(verb)) {
                SemanticNode semanticNode = node.getLinkedNode(semanticCase);
                if (semanticNode != null) {
                    sb.append(semanticNode.getValue());
                }
            }
        }
        return sb.toString();
    }

    private String getSemanticCase(String nCase) {
        switch (nCase) {
            case MorphInfo.IM:
                return Semantic.SUBJECT;
            case MorphInfo.RD:
                return Semantic.ADDITION_WHOM;
            case MorphInfo.DT:
                return Semantic.ADDITION_TO;
            case MorphInfo.VN:
                return Semantic.OBJECT;
            case MorphInfo.TV:
                return Semantic.ADDITION_BY;
            case MorphInfo.PR:
                return Semantic.ADDITION_ABOUT;
            default:
                return null;
        }
    }

    private String getNCaseOf(String message, String token) {
        String[] words = StringPreProcessor.split(message);
        for (int i = 0; i < words.length; i++) {
            List<MorphInfo> infoList = MorphUtils
                    .parse(morphology.getMorphInfo(words[i]));
            for (MorphInfo info : infoList) {
                if (info.getStandartForm().equals(token)
                        && canBeSOA(info)) return info.getnCase();
            }
        }
        return null;
    }

    private String features(MessageDTO msg, int verbIndex, int subjIndex, int objIndex) {
        Bot b = chatService.findBotInChat(msg.getChatId());
        StringBuilder ans = new StringBuilder();
        String subj = null, obj = null, verb = null;
        if (subjIndex != -1) {
            subj = StringPreProcessor.split(msg.getMessage())[subjIndex];
        }
        if (objIndex != -1) {
            obj = StringPreProcessor.split((msg.getMessage()))[objIndex];
        }
        if (verbIndex != -1) {
            verb = StringPreProcessor.split(msg.getMessage())[verbIndex];
        }
        int afterpos = getIndexOf(msg.getMessage(), KIND_OF);
        String kindOfWord = kindOfWord(msg.getMessage(), afterpos);

        for (SemanticNetwork network : b.getNetworks()) {
            ans.append(findInNetworkFeatures(network, verb, kindOfWord));
        }
        return ans.toString().equals("") ? null : ans.toString().trim();
    }

    private String kindOfWord(String s, int afterpos) {
        String[] words = StringPreProcessor.split(s);
        for (int i = afterpos + 1; i < words.length; i++) {
            if (suitFeature(words[afterpos], words[i])) return words[i];
        }
        return null;
    }

    private String findInNetworkFeatures(SemanticNetwork network, String verb, String word) {
        StringBuilder sb = new StringBuilder();
        for (SemanticNode node : network.getNodes()) {
            if (node.getPeak() && node.getValue().equals(verb)) {
                for (SemanticLink link : node.getLinks()) {
                    if ((link.getValue().equals(Semantic.SUBJECT)
                            || link.getValue().equals(Semantic.OBJECT))
                            && link.getLinkedNode().getValue().equals(word)) {
                        sb.append(SPACE)
                                .append(link.getLinkedNode()
                                        .getLinkedNode(Semantic.FEATURE)
                                        .getValue());
                    }
                }
            }
        }
        return sb.toString();
    }

    private String images(MessageDTO msg, int verbIndex, int subjIndex, int objIndex) {
        Bot b = chatService.findBotInChat(msg.getChatId());
        StringBuilder ans = new StringBuilder();
        String subj = null, obj = null;
        String verb = StringPreProcessor.split(msg.getMessage())[verbIndex];
        if (subjIndex != -1) {
            subj = StringPreProcessor.split(msg.getMessage())[subjIndex];
        }
        if (objIndex != -1) {
            obj = StringPreProcessor.split((msg.getMessage()))[objIndex];
        }
        for (SemanticNetwork network : b.getNetworks()) {
            ans.append(findInNetworkImages(network, verb, subj, obj));
        }
        return ans.toString().equals("") ? null : ans.toString().trim();
    }

    private String findInNetworkImages(SemanticNetwork network, String verb, String subj, String obj) {
        StringBuilder sb = new StringBuilder();
        Set<SemanticNode> nodes = network.getNodes();
        for (SemanticNode node : nodes) {
            if (node.getPeak() && node.getValue().equals(verb)) {
                if (thisSubject(node, subj) && thisObject(node, obj)) {
                    for (SemanticLink link : node.getLinks()) {
                        if (link.getValue().equals(Semantic.IMAGE)) {
                            sb.append(SPACE)
                                    .append(link.getLinkedNode().getValue());
                        }
                    }
                }
            }
        }
        return sb.toString();
    }

    private boolean thisSubject(SemanticNode node, String subj) {
        if (subj == null) return true;
        for (SemanticLink link : node.getLinks()) {
            if (link.getValue().equals(Semantic.SUBJECT)
                    && link.getLinkedNode().getValue().equals(subj)) return true;
        }
        return false;
    }

    private boolean thisObject(SemanticNode node, String obj) {
        if (obj == null) return true;
        for (SemanticLink link : node.getLinks()) {
            if (link.getValue().equals(Semantic.OBJECT)
                    && link.getLinkedNode().getValue().equals(obj)) return true;
        }
        return false;
    }

    private int getIndexOf(String message, String token) {
        String[] words = StringPreProcessor.split(message);
        for (int i = 0; i < words.length; i++) {
            List<MorphInfo> infoList = MorphUtils
                    .parse(morphology.getMorphInfo(words[i]));
            for (MorphInfo info : infoList) {
                if (info.getStandartForm().equals(token)) return i;
            }
        }
        return -1;
    }

    private boolean qwContains(String s, String token) {
        String[] words = StringPreProcessor.split(s);
        for (String word : words) {
            List<MorphInfo> infoList = MorphUtils
                    .parse(morphology.getMorphInfo(word));
            for (MorphInfo info : infoList) {
                if (info.getStandartForm().equals(token)) return true;
            }
        }
        return false;
    }

    private boolean isQuestion(String message) {
        if (message.contains("?")) return true;
        return false;
    }

    private void divideIntoNodesAndSave(String message,
                                        SemanticNetwork network) {
        String[] words = StringPreProcessor.split(message);
        if (countVerbs(words) == 1) {
            findSPO(words, network);
        }
    }

    private void findSPO(String[] words, SemanticNetwork network) {
        int predIndex = findVerb(words);
        if (predIndex == -1) return;
        int subjIndex = findSubject(words, predIndex);
        if (subjIndex == -1) return;
        int objIndex = findObject(words, subjIndex, predIndex);
        createNetwork(words, predIndex, subjIndex, objIndex, network);
    }

    private void createNetwork(String[] words, int predIndex, int subjIndex,
                               int objIndex, SemanticNetwork network) {
        String fullPred = getFullPredicate(words, predIndex);
        SemanticNode predicate = createPredicate(fullPred, network);
        SemanticNode subject = createNode(words[subjIndex], predicate,
                Semantic.SUBJECT, network);
        linkFeatures(words, subject, subjIndex, network);

        if (objIndex != -1) {
            String fullObj = linkPreposition(words, objIndex);
            SemanticNode object = createNode(fullObj, predicate,
                    Semantic.OBJECT, network);
            linkFeatures(words, object, objIndex, network);
        }

        linkImages(words, predicate, network);
        linkAdditions(words, predicate, network, objIndex, subjIndex);
    }

    private void linkAdditions(String[] words, SemanticNode predicate,
                               SemanticNetwork network, int objIndex,
                               int subjIndex) {
        for (int i = 0; i < words.length; i++) {
            if (i != objIndex && i != subjIndex) {
                SemanticNode addition = null;
                String fullAdd = linkPreposition(words, i);
                if (canBeAdditionWhom(words[i])) {
                    addition = createNode(fullAdd, predicate, Semantic.ADDITION_WHOM, network);
                } else if (canBeAdditionTo(words[i])) {
                    addition = createNode(fullAdd, predicate, Semantic.ADDITION_TO, network);
                } else if (canBeAdditionBy(words[i])) {
                    addition = createNode(fullAdd, predicate, Semantic.ADDITION_BY, network);
                } else if (canBeAdditionAbout(words[i])) {
                    addition = createNode(fullAdd, predicate, Semantic.ADDITION_ABOUT, network);
                }
                if (addition != null) {
                    linkFeatures(words, addition, i, network);
                }
            }
        }
    }

    private void linkImages(String[] words, SemanticNode predicate, SemanticNetwork network) {
        for (int i = 0; i < words.length; i++) {
            if (canBeImage(words[i])) {
                createNode(words[i], predicate, Semantic.IMAGE, network);
            }
        }
    }

    private void linkFeatures(String[] words, SemanticNode parent, int index,
                              SemanticNetwork network) {
        for (int i = index - 1; i >= 0; i--) {
            if (canBeFeature(words[i]) && suitFeature(words[i], words[index])) {
                createNode(words[i], parent, Semantic.FEATURE, network);
            }
        }
    }

    private boolean suitFeature(String feat, String word) {
        List<MorphInfo> featList = MorphUtils
                .parse(morphology.getMorphInfo(feat));
        List<MorphInfo> wordList = MorphUtils
                .parse(morphology.getMorphInfo(word));
        for (MorphInfo featInfo : featList) {
            for (MorphInfo wordInfo : wordList) {
                if (canBeFeature(featInfo)) {
                    if (featInfo.getCount().equals(MorphInfo.MN)
                            && featInfo.checkCount(wordInfo.getCount())
                            && featInfo.checkNCase(wordInfo.getnCase())) {
                        return true;
                    } else {
                        if (featInfo.checkGender(wordInfo.getGender())
                                && featInfo.checkNCase(wordInfo.getnCase())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean canBeFeature(String word) {
        List<MorphInfo> infoList = MorphUtils
                .parse(morphology.getMorphInfo(word));
        for (MorphInfo info : infoList) {
            if (canBeFeature(info)) {
                return true;
            }
        }
        return false;
    }

    private boolean canBeImage(String word) {
        List<MorphInfo> list = MorphUtils
                .parse(morphology.getMorphInfo(word));
        for (MorphInfo info : list) {
            if (canBeImage(info)) {
                return true;
            }
        }
        return false;
    }

    private String linkPreposition(String[] words, int objIndex) {
        if (!havePrepForIndex(words, objIndex)) {
            return words[objIndex];
        } else {
            for (int i = objIndex - 1; i >= 0; i--) {
                if (isPreposition(words[i])) {
                    return words[i] + SPACE + words[objIndex];
                }
            }
        }
        return words[objIndex];
    }

    private boolean isPreposition(String word) {
        List<MorphInfo> infoList = MorphUtils
                .parse(morphology.getMorphInfo(word));
        for (MorphInfo info : infoList) {
            if (info.checkPartOfSpeech(MorphInfo.PREDL)) {
                return true;
            }
        }
        return false;
    }

    private String getFullPredicate(String[] words, int predIndex) {
        StringBuilder sb;
        if (predIndex > 0 && words[predIndex - 1].equals(NOT)) {
            sb = new StringBuilder(NOT);
            sb.append(SPACE).append(words[predIndex]);
        } else {
            sb = new StringBuilder(words[predIndex]);
        }
        for (int i = predIndex + 1; i < words.length; i++) {
            if (words[i].equals(IF)) {
                sb.append(SPACE).append(IF);
            }
            if (isInfinitive(words[i])) {
                if (words[i - 1].equals(NOT)) {
                    sb.append(SPACE).append(NOT)
                            .append(SPACE).append(words[i]);
                } else {
                    sb.append(SPACE).append(words[i]);
                }
            }
        }
        return sb.toString();
    }

    private boolean isInfinitive(String word) {
        List<MorphInfo> infoList = MorphUtils
                .parse(morphology.getMorphInfo(word));
        for (MorphInfo info : infoList) {
            if (info.checkPartOfSpeech(MorphInfo.INFINITIV)) {
                return true;
            }
        }
        return false;
    }

    private SemanticNode createNode(String word, SemanticNode parent,
                                    String link, SemanticNetwork network) {
        SemanticNode node = new SemanticNode();
        node.setNetwork(network);
        node.setValue(word);
        SemanticLink sLink = new SemanticLink();
        sLink.setLinkedNode(node);
        sLink.setNode(parent);
        sLink.setValue(link);
        semanticLinkDao.save(sLink);
        return node;
    }

    private SemanticNode createPredicate(String word,
                                         SemanticNetwork network) {
        SemanticNode node = new SemanticNode();
        node.setNetwork(network);
        node.setValue(word);
        node.setPeak(true);
        return node;
    }

    private int findObject(String[] words, int subjIndex, int predIndex) {
        for (int i = 0; i < words.length; i++) {
            List<MorphInfo> infoList = MorphUtils
                    .parse(morphology.getMorphInfo(words[i]));

            for (MorphInfo info : infoList) {
                if (canBeObject(info) && i != subjIndex && i != predIndex
                        && !qwWords(info)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private boolean qwWords(MorphInfo info) {
        String std = info.getStandartForm();
        return std.equals(WHAT) || std.equals(WHO) || std.equals(HOW)
                || std.equals(KIND_OF);
    }

    private boolean havePrepForIndex(String[] words, int index) {
        boolean thisWord = true;
        for (int i = index - 1; i >= 0; i--) {
            List<MorphInfo> infoList = MorphUtils
                    .parse(morphology.getMorphInfo(words[i]));
            for (MorphInfo info : infoList) {
                if (info.checkPartOfSpeech(MorphInfo.PREDL) && thisWord) {
                    return true;
                }
                if (shouldStopFindingPrep(info)) {
                    thisWord = false;
                }
            }
        }
        return false;
    }

    private boolean shouldStopFindingPrep(MorphInfo info) {
        return info.checkPartOfSpeech(MorphInfo.S)
                || info.checkPartOfSpeech(MorphInfo.MS)
                || info.checkPartOfSpeech(MorphInfo.G);
    }

    private int findSubject(String[] words, int predIndex) {
        for (int i = 0; i < words.length; i++) {
            if (canBeSubject(words[i])
                    && suitSubject(words, i, predIndex)) {
                return i;
            }
        }
        return -1;
    }

    private boolean suitSubject(String[] words, int wIndex, int predIndex) {
        if (predIndex == -1) return false;
        if (words[wIndex].equals(I) || words[wIndex].equals(YOU)) {
            return true;
        }
        List<MorphInfo> wordInfo = MorphUtils
                .parse(morphology.getMorphInfo(words[wIndex]));
        List<MorphInfo> predInfo = MorphUtils
                .parse(morphology.getMorphInfo(words[predIndex]));
        for (MorphInfo winfo : wordInfo) {
            for (MorphInfo pinfo : predInfo) {
                if (canBeSubject(winfo) && canBePredicate(pinfo)
                        && !qwWords(winfo)) {
                    if (!pinfo.checkTime(MorphInfo.PRSH)
                            && winfo.checkCount(pinfo.getCount())
                            && !havePrepForIndex(words, wIndex)) {
                        return true;
                    } else if (pinfo.checkCount(MorphInfo.MN)
                            && winfo.checkCount(pinfo.getCount())
                            && !havePrepForIndex(words, wIndex)) {
                        return true;
                    } else {
                        if (pinfo.checkTime(MorphInfo.PRSH) && winfo
                                .checkGender(getPredGender(words[predIndex]))
                                && !havePrepForIndex(words, wIndex)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean canBePredicate(MorphInfo pinfo) {
        return pinfo.checkPartOfSpeech(MorphInfo.G);
    }

    private boolean canBeSubject(String word) {
        List<MorphInfo> infoList = MorphUtils
                .parse(morphology.getMorphInfo(word));
        for (MorphInfo info : infoList) {
            if (canBeSubject(info)) {
                return true;
            }
        }
        return false;
    }

    private String getPredGender(String word) {
        List<MorphInfo> pred = MorphUtils
                .parse(morphology.getMorphInfo(word));
        for (MorphInfo pinfo : pred) {
            if (pinfo.checkPartOfSpeech(MorphInfo.G)) {
                return pinfo.getGender();
            }
        }
        return null;
    }

    private int findVerb(String[] words) {
        for (int i = 0; i < words.length; i++) {
            if (canBePredicate(words[i])) {
                return i;
            }
        }
        return -1;
    }

    private int countVerbs(String[] words) {
        int rez = 0;
        for (String s : words) {
            if (canBePredicate(s)) {
                rez++;
            }
        }
        return rez;
    }

    private boolean canBeCounter(MorphInfo info) {
        return info.checkPartOfSpeech(MorphInfo.CHISL);
    }

    private boolean canBeSubject(MorphInfo info) {
        return (info.checkPartOfSpeech(MorphInfo.S)
                || info.checkPartOfSpeech(MorphInfo.MS))
                && info.checkNCase(MorphInfo.IM);
    }

    private boolean canBeSOA(MorphInfo info) {
        return (info.checkPartOfSpeech(MorphInfo.S)
                || info.checkPartOfSpeech(MorphInfo.MS));
    }

    private boolean canBePredicate(String word) {
        List<MorphInfo> infoList = MorphUtils
                .parse(morphology.getMorphInfo(word));
        for (MorphInfo info : infoList) {
            if (info.checkPartOfSpeech(MorphInfo.G)) {
                return true;
            }
        }
        return false;
    }

    private boolean canBeImage(MorphInfo info) {
        return info.checkPartOfSpeech(MorphInfo.N)
                || info.checkPartOfSpeech(MorphInfo.DEEPRICHASTIE);
    }

    private boolean canBeObject(MorphInfo info) {
        return (info.checkPartOfSpeech(MorphInfo.S)
                || info.checkPartOfSpeech(MorphInfo.MS))
                && info.checkNCase(MorphInfo.VN);
    }

    private boolean canBeAdditionWhom(String word) {
        List<MorphInfo> list = MorphUtils
                .parse(morphology.getMorphInfo(word));
        for (MorphInfo info : list) {
            if (canBeAdditionWhom(info)) {
                return true;
            }
        }
        return false;
    }

    private boolean canBeAdditionWhom(MorphInfo info) {
        return (info.checkPartOfSpeech(MorphInfo.S)
                || info.checkPartOfSpeech(MorphInfo.MS))
                && info.checkNCase(MorphInfo.RD);
    }

    private boolean canBeAdditionTo(String word) {
        List<MorphInfo> list = MorphUtils
                .parse(morphology.getMorphInfo(word));
        for (MorphInfo info : list) {
            if (canBeAdditionTo(info)) {
                return true;
            }
        }
        return false;
    }

    private boolean canBeAdditionTo(MorphInfo info) {
        return (info.checkPartOfSpeech(MorphInfo.S)
                || info.checkPartOfSpeech(MorphInfo.MS))
                && info.checkNCase(MorphInfo.DT);
    }

    private boolean canBeAdditionBy(String word) {
        List<MorphInfo> list = MorphUtils
                .parse(morphology.getMorphInfo(word));
        for (MorphInfo info : list) {
            if (canBeAdditionBy(info)) {
                return true;
            }
        }
        return false;
    }

    private boolean canBeAdditionBy(MorphInfo info) {
        return (info.checkPartOfSpeech(MorphInfo.S)
                || info.checkPartOfSpeech(MorphInfo.MS))
                && info.checkNCase(MorphInfo.TV);
    }

    private boolean canBeAdditionAbout(String word) {
        List<MorphInfo> list = MorphUtils
                .parse(morphology.getMorphInfo(word));
        for (MorphInfo info : list) {
            if (canBeAdditionAbout(info)) {
                return true;
            }
        }
        return false;
    }

    private boolean canBeAdditionAbout(MorphInfo info) {
        return (info.checkPartOfSpeech(MorphInfo.S)
                || info.checkPartOfSpeech(MorphInfo.MS))
                && info.checkNCase(MorphInfo.PR);
    }

    private boolean canBeFeature(MorphInfo info) {
        return info.checkPartOfSpeech(MorphInfo.P)
                || info.checkPartOfSpeech(MorphInfo.PRICHASTIE)
                || info.checkPartOfSpeech(MorphInfo.KR_PRICHASTIE)
                || info.checkPartOfSpeech(MorphInfo.KR_PRIL)
                || info.checkPartOfSpeech(MorphInfo.CHISL_P)
                || info.checkPartOfSpeech(MorphInfo.MS_P);
    }
}
