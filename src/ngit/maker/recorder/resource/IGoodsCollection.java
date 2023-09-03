package ngit.maker.recorder.resource;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IGoodsCollection {
    public static final List<String> GOOD_WORDS;
    static {
        GOOD_WORDS = new ArrayList<>();
        GOOD_WORDS.add("[你知道吗]这里是回声洞，目前只有些励志句子呢...");
        GOOD_WORDS.add("[你知道吗]想参与可以联系Maker哦！QQ:3568584766");
        GOOD_WORDS.add("既然选择远方，当不负青春，砥砺前行。");
        GOOD_WORDS.add("青春由磨砺而出彩，人生因奋斗而升华！");
        GOOD_WORDS.add("但行前路，不负韶华！");
        GOOD_WORDS.add("每一个裂缝都是为透出光而努力。");
        GOOD_WORDS.add("美好的一天，上帝不会就这样给你，需要自己去创造。");
        GOOD_WORDS.add("你要做冲出的黑马 而不是坠落的星星。");
        GOOD_WORDS.add("纵然世间黑暗，仍有一点星光。");
        GOOD_WORDS.add("试一下，你会比你自己想象中的还要强大。");
        GOOD_WORDS.add("眼里有不朽的光芒 心里有永恒的希望。");
        GOOD_WORDS.add("等待的不仅仅是未来，还有希望。");
        GOOD_WORDS.add("只有极致的拼搏，才能配得上极致的风景。");
        GOOD_WORDS.add("如果痛恨所处的黑暗，请你成为你想要的光。 —— 顾城");
        GOOD_WORDS.add("以蝼蚁之行，展鸿鹄之志。");
        GOOD_WORDS.add("一个人至少拥有一个梦想，有一个理由去坚强。");
        GOOD_WORDS.add("你可以一无所有，但绝不能一无是处。");
        GOOD_WORDS.add("努力的时间还不够 哪有时间去绝望啊？ —— 余安");
        GOOD_WORDS.add("彗星般的人生可以短暂，但绝不黯淡或沉沦。 —— 纳兰容若");
        GOOD_WORDS.add("抱怨身处黑暗，不如提灯前行。 —— 刘同《向着光亮那方》");
        GOOD_WORDS.add("黑暗的笼罩更会凸显光明的可贵。");
        GOOD_WORDS.add("你要成长，绝处也能逢生。");
        GOOD_WORDS.add("心态决定高度，细节决定成败。");
        GOOD_WORDS.add("没人会嘲笑竭尽全力的人。 —— 《家庭教师》");
        GOOD_WORDS.add("上天是公平的，有付出就有收获。");
        GOOD_WORDS.add("生活很苦，但不要放弃爱与希望。 —— 《送你一朵小红花》");
        GOOD_WORDS.add("过去的价值不代表未来的地位。");
        GOOD_WORDS.add("惟有主动付出，才有丰富的果实获得收获。");
        GOOD_WORDS.add("站在死亡面前并不可怕，可怕的是不能牺牲的有所价值。");
        GOOD_WORDS.add("存在是因为价值创造！淘汰是因为价值丧失。");
        GOOD_WORDS.add("价值的真正意义在于宁愿牺牲自己，也不愿拖累他人的精神。");
        GOOD_WORDS.add("什么都不懂的人是毫无价值的。");
        GOOD_WORDS.add("无私的奉献不仅可以帮助别人，也会在无形中提升自己的价值。");
        GOOD_WORDS.add("价值不等同于付出，但是付出却可以换来价值。");
        GOOD_WORDS.add("任务在无形中完成，价值在无形中升华。");
        GOOD_WORDS.add("当我们远离了言语与是非，我们的一切存在也就真实地显露了本来的价值。");
        GOOD_WORDS.add("你若喜爱你本身的价值，那么你就得给世界创造价值。");
        GOOD_WORDS.add("人们总是喜欢高估自己所没有的东西的价值，而忽视自己本身所拥有的东西。");
        GOOD_WORDS.add("精彩的人生是在有限生命中实现无限价值的人生。");
        GOOD_WORDS.add("人一生的价值，不应该用时间去衡量，而是用深度去衡量。");
        GOOD_WORDS.add("成功最重要的因素是要有一个健康的身体和旺盛的精力。");
        GOOD_WORDS.add("成功是陡峭的阶梯，两手插在裤袋里是爬不上去的。");
        GOOD_WORDS.add("成功没有奇迹，只有轨迹；成功不靠条件，只靠信念！");
        GOOD_WORDS.add("成功就是失败到失败，也丝毫不减当初的热情。");
        GOOD_WORDS.add("成功的关键，在于勇敢承担责任。");
        GOOD_WORDS.add("成功的道路别自己一个人摸索，只有多问路才不会迷路。");
        GOOD_WORDS.add("努力不一定成功，但放弃一定会失败。");
        GOOD_WORDS.add("自己战胜自己是最可贵的胜利。");
        GOOD_WORDS.add("人生虽曲折，记得活出精彩。");
        GOOD_WORDS.add("怕的不是做不到，而是想都不敢想。");
        GOOD_WORDS.add("此刻打盹，你将做梦；此刻学习，你将圆梦。");
        GOOD_WORDS.add("得之坦然，失之淡然，争取必然，顺其自然。");
        GOOD_WORDS.add("世界的模样，取决你凝视它的目光。");
        GOOD_WORDS.add("现在睡觉的话会做梦，而现在学习的话会让梦实现。");
        GOOD_WORDS.add("那些比我强大的人都还在拼命。我有什麽理由不去努力。");
        GOOD_WORDS.add("当你想要放弃的时候想想当初为什么坚持到这里。");
        GOOD_WORDS.add("以良好的心态面对生活，你的生活才美好。");
        GOOD_WORDS.add("把握现在、就是创造未来。");
        GOOD_WORDS.add("人最大的对手，就是自己的懒惰。");
        GOOD_WORDS.add("人生不求与人相比，但求超越自己。");
        GOOD_WORDS.add("无论何时，不管怎样，我也绝不允许自己有一点点心丧气。 —— 爱迪生");
        GOOD_WORDS.add("笨鸟先飞早入林，笨人勤学早成材。 —— 《省世格言》");
        GOOD_WORDS.add("成功是由日复一日的点滴努力汇聚而成的。");
        GOOD_WORDS.add("一个有坚强心志的人，财产可以被人掠夺，勇气却不会被人剥夺的。 —— 雨果");
        GOOD_WORDS.add("迈开脚步，再长的路也不在话下；停滞不前，再短的路也难以到达。");
        GOOD_WORDS.add("梦想从这刻起，并不只是个幻想，靠自己它能成为现实中的一部分。");
        GOOD_WORDS.add("为了未来好一点，现在苦一点有什么。");
        GOOD_WORDS.add("只有承担起旅途风雨，最终才能守得住彩虹满天。");
        GOOD_WORDS.add("苦想没有盼头，苦干才有奔头。");
        GOOD_WORDS.add("所有的努力 不是为了让别人觉得你了不起 而是让自己过得充实而有追求");
        GOOD_WORDS.add("坚持意志伟大的事业需要始终不渝的精神。 —— 伏尔泰");
        GOOD_WORDS.add("不要垂头丧气，即使失去一切，明天仍在你的手里。 —— 奥丅斯卡·王尔德");
        GOOD_WORDS.add("欲穷千里目，更上一层楼。 —— 王之涣");
        GOOD_WORDS.add("第一个青春是上天给的；第二个的青春是靠自己努力的。");
        GOOD_WORDS.add("每个梦想，都是在现实中坚持不懈才实现的。");
        GOOD_WORDS.add("只要路是对的，就不怕路远。");
        GOOD_WORDS.add("成功的秘诀之一就是不让暂时的挫折击垮我们。");
        GOOD_WORDS.add("只有不断找寻机会的人才会及时把握机会。");
        GOOD_WORDS.add("没有口水与汗水，就没有成功的泪水。");
        GOOD_WORDS.add("勤奋是你生命的密码，能译出你一部壮丽的史诗。");
        GOOD_WORDS.add("志在山顶的人，不会贪念山腰的风景。");
        GOOD_WORDS.add("所有的胜利，与征服自己的胜利比起来，都是微不足道。");
        GOOD_WORDS.add("人生最宝贵的不是你拥有多少的物质，而是陪伴在你身边的人。");
        GOOD_WORDS.add("奋斗者在汗水汇集的江河里，将事业之舟驶到了理想的彼岸。");
        GOOD_WORDS.add("生活的道路一旦选定，就要勇敢地走到底，决不回头。");
        GOOD_WORDS.add("天才是百分之一的灵感加上百分之九十九的努力。");
        GOOD_WORDS.add("只有经历地狱般的磨练，才能炼出创造天堂的力量。");
        GOOD_WORDS.add("萤火虫的光点虽然微弱，但亮着便是向黑暗挑战。");
        GOOD_WORDS.add("在很多人看来，失败是可耻的，但其实，失败才是常态。");
    }

    public static ImageIcon getIcon(Logger logger){
        ImageIcon ico = null;
        try {
             ico = new ImageIcon(Objects.requireNonNull(IGoodsCollection.class.getResource("img/ico.png")));
        } catch (NullPointerException e) {
            logger.log(Level.WARNING, "Couldn't load icon image, see more: ", e);
        }
        return ico;
    }

    public static ImageIcon getFund(Logger logger){
        ImageIcon ico = null;
        try {
            ico = new ImageIcon(Objects.requireNonNull(IGoodsCollection.class.getResource("img/subscribe.png")));
        } catch (NullPointerException e) {
            logger.log(Level.WARNING, "Couldn't load icon image from source, see more: ", e);
            String dldTarget = "https://makertechno.github.io/resources/subscribe.png";
            try {
                BufferedImage image = ImageIO.read(new URL(dldTarget));
                ico = new ImageIcon(image);
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Couldn't load icon image from url, see more: ", e);
            }
        }
        return ico;
    }
}
