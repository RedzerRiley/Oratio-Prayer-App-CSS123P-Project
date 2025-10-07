// File: src/main/java/com/oratio/utils/FileUtils.java
package com.oratio.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for file operations
 */
public class FileUtils {

    /**
     * Load a resource file from the classpath or file system
     */
    public static String loadResourceFile(String fileName) {
        try {
            // First try to load from classpath (for packaged application)
            InputStream inputStream = FileUtils.class.getClassLoader()
                    .getResourceAsStream(fileName);

            if (inputStream != null) {
                return readInputStream(inputStream);
            }

            // If not found in classpath, try to load from file system
            Path filePath = Paths.get(Constants.RESOURCES_PATH + fileName);
            if (Files.exists(filePath)) {
                return Files.readString(filePath, StandardCharsets.UTF_8);
            }

            // Return default content for common prayers if file not found
            return getDefaultContent(fileName);

        } catch (IOException e) {
            System.err.println("Error loading resource file: " + fileName);
            e.printStackTrace();
            return getDefaultContent(fileName);
        }
    }

    /**
     * Read content from InputStream
     */
    private static String readInputStream(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    /**
     * Get default content for essential prayers when files are not available
     */
    private static String getDefaultContent(String fileName) {
        switch (fileName.toLowerCase()) {
            case "prayers_english.txt":
                return getDefaultEnglishPrayers();
            case "prayers_tagalog.txt":
                return getDefaultTagalogPrayers();
            case "prayers_latin.txt":
                return getDefaultLatinPrayers();
            case "rosary_english.txt":
                return getDefaultEnglishRosary();
            case "rosary_tagalog.txt":
                return getDefaultTagalogRosary();
            case "rosary_latin.txt":
                return getDefaultLatinRosary();
            case "psalms_english.txt":
                return getDefaultEnglishPsalms();
            default:
                return "Content not available for: " + fileName;
        }
    }

    private static String getDefaultEnglishPrayers() {
        return """
                our_father
                Our Father, who art in heaven,
                hallowed be thy name.
                Thy kingdom come,
                thy will be done,
                on earth as it is in heaven.
                Give us this day our daily bread,
                and forgive us our trespasses,
                as we forgive those who trespass against us,
                and lead us not into temptation,
                but deliver us from evil.
                Amen.
                
                ===
                
                hail_mary
                Hail Mary, full of grace,
                the Lord is with thee.
                Blessed art thou amongst women,
                and blessed is the fruit of thy womb, Jesus.
                Holy Mary, Mother of God,
                pray for us sinners,
                now and at the hour of our death.
                Amen.
                
                ===
                
                glory_be
                Glory be to the Father,
                and to the Son,
                and to the Holy Spirit,
                as it was in the beginning,
                is now, and ever shall be,
                world without end.
                Amen.
                
                ===
                
                morning_offering
                O Jesus, through the Immaculate Heart of Mary,
                I offer you my prayers, works, joys, and sufferings
                of this day for all the intentions of your Sacred Heart,
                in union with the Holy Sacrifice of the Mass
                throughout the world,
                in reparation for my sins,
                for the intentions of all my relatives and friends,
                and in particular for the intentions of the Holy Father.
                Amen.
                
                ===
                
                evening_prayer
                O my God, I thank you for having preserved me today.
                I offer you all the good I have done,
                and I ask pardon for all the evil I have committed.
                Protect me this night and may your grace be with me always.
                To you I commend my family, my friends, and all those in need.
                Amen.
                
                ===
                
                act_of_contrition
                O my God, I am heartily sorry for having offended Thee,
                and I detest all my sins because of thy just punishments,
                but most of all because they offend Thee, my God,
                who art all good and deserving of all my love.
                I firmly resolve with the help of Thy grace
                to sin no more and to avoid the near occasion of sin.
                Amen.
                """;
    }

    private static String getDefaultTagalogPrayers() {
        return """
            our_father
            Ama namin, sumasalangit Ka,
            sambahin ang ngalan Mo.
            Mapasaamin ang kaharian Mo.
            Sundin ang loob Mo,
            dito sa lupa para nang sa langit.
            Bigyan Mo kami ngayon ng aming kakanin sa araw-araw,
            at patawarin Mo kami sa aming mga sala,
            para nang pagpapatawad namin
            sa nagsasala sa amin,
            at huwag Mo kaming ipahintulot sa tukso,
            at iadya Mo kami sa lahat ng masama.
            Amen.

            ===

            hail_mary
            Aba Ginoong Maria, napupuno ka ng grasya,
            ang Panginoon ay sumasaiyo.
            Pinagpala ka sa mga babae,
            at pinagpala naman ang bunga ng iyong tiyan na si Jesus.
            Santa Maria, Ina ng Diyos,
            ipanalangin mo kaming makasalanan,
            ngayon at kung kami ay mamamatay.
            Amen.

            ===

            glory_be
            Luwalhati sa Ama,
            at sa Anak,
            at sa Espiritu Santo.
            Gaya nang sa pasimula,
            ngayon at kailanman,
            at sa walang hanggang panahon.
            Amen.

            ===

            morning_offering
            O Hesus, sa pamamagitan ng Kalinis-linisang Puso ni Maria,
            iniaalay ko sa Iyo ang aking mga panalangin, gawa, tuwa, at paghihirap
            sa araw na ito, para sa lahat ng layunin ng Iyong Kamahal-mahalang Puso,
            kaisa ng banal na Misa na iniaalay sa buong mundo,
            bilang bayad sa aking mga kasalanan,
            para sa ikabubuti ng aking mga kamag-anak at kaibigan,
            at lalo na para sa mga layunin ng Santo Papa.
            Amen.

            ===

            act_of_contrition
            O Diyos ko, ako’y taos-pusong nagsisisi
            sa pagkakasala sa Iyo, at kinasusuklaman ko ang lahat kong kasalanan,
            dahil sa Iyong makatarungang parusa,
            higit sa lahat dahil sa Ikaw ay labis kong iniibig
            at ayaw kong mawala ang Iyong pagmamahal.
            Tinatalaga kong sa tulong ng Iyong biyaya,
            ako’y hindi na muling magkakasala
            at lalayo sa anumang pagkakataon ng pagkakasala.
            Amen.
            """;
    }

    private static String getDefaultLatinPrayers() {
        return """
            our_father
            Pater noster, qui es in caelis,
            sanctificetur nomen tuum.
            Adveniat regnum tuum.
            Fiat voluntas tua,
            sicut in caelo et in terra.
            Panem nostrum quotidianum da nobis hodie,
            et dimitte nobis debita nostra,
            sicut et nos dimittimus debitoribus nostris,
            et ne nos inducas in tentationem,
            sed libera nos a malo.
            Amen.

            ===

            hail_mary
            Ave Maria, gratia plena,
            Dominus tecum.
            Benedicta tu in mulieribus,
            et benedictus fructus ventris tui, Iesus.
            Sancta Maria, Mater Dei,
            ora pro nobis peccatoribus,
            nunc et in hora mortis nostrae.
            Amen.

            ===

            glory_be
            Gloria Patri,
            et Filio,
            et Spiritui Sancto.
            Sicut erat in principio,
            et nunc et semper,
            et in saecula saeculorum.
            Amen.

            ===

            morning_offering
            Iesu mi, per Cor Immaculatum Mariae,
            offero Tibi preces, opera, gaudia et dolores huius diei,
            pro omnibus intentionibus Cordis Tui Sacratissimi,
            in unione cum Sacrificio Missae per totum mundum celebrato,
            in reparationem peccatorum meorum,
            pro salute propinquorum et amicorum meorum,
            atque in particulari pro intentionibus Summi Pontificis.
            Amen.

            ===

            act_of_contrition
            Deus meus, ex toto corde paenitet me omnium meorum peccatorum,
            eaque detestor, quia peccando non solum poenas a Te iuste statutas meritus sum,
            sed praesertim quia offendi Te, summum bonum,
            ac dignum qui super omnia diligaris.
            Ideo firmiter propono, adiuvante gratia Tua,
            de cetero me non peccaturum esse
            ac de proximis peccandi occasionibus me esse fugiturum.
            Amen.
            """;
    }
    private static String getDefaultEnglishRosary() {
        return """
                sign_cross
                In the name of the Father, and of the Son, and of the Holy Spirit. Amen.
                
                ===
                
                apostles_creed
                I believe in God, the Father Almighty, Creator of heaven and earth,
                and in Jesus Christ, His only Son, our Lord,
                who was conceived by the Holy Spirit, born of the Virgin Mary,
                suffered under Pontius Pilate, was crucified, died and was buried;
                He descended into hell; on the third day He rose again from the dead;
                He ascended into heaven, and is seated at the right hand of God the Father Almighty;
                from there He will come to judge the living and the dead.
                I believe in the Holy Spirit, the Holy Catholic Church,
                the communion of saints, the forgiveness of sins,
                the resurrection of the body, and life everlasting. Amen.
                
                ===
                
                fatima_prayer
                O my Jesus, forgive us our sins, save us from the fires of hell,
                and lead all souls to Heaven, especially those in most need of Thy mercy.
                
                ===
                
                hail_holy_queen
                Hail, Holy Queen, Mother of mercy, our life, our sweetness, and our hope.
                To thee do we cry, poor banished children of Eve.
                To thee do we send up our sighs, mourning and weeping in this valley of tears.
                Turn then, most gracious advocate, thine eyes of mercy toward us,
                and after this our exile, show unto us the blessed fruit of thy womb, Jesus.
                O clement, O loving, O sweet Virgin Mary.
                Pray for us, O holy Mother of God,
                that we may be made worthy of the promises of Christ.
                
                ===
                
                joyful_mystery_1
                The Annunciation
                
                ===
                
                joyful_mystery_2
                The Visitation
                
                ===
                
                joyful_mystery_3
                The Nativity
                
                ===
                
                joyful_mystery_4
                The Presentation
                
                ===
                
                joyful_mystery_5
                The Finding in the Temple
                """;
    }

    private static String getDefaultTagalogRosary() {
        return """
                sign_cross
                Sa pangalan ng Ama, at ng Anak, at ng Espiritu Santo. Amen.
                
                ===
                
                joyful_mystery_1
                Ang Pagkabalita kay Maria
                
                ===
                
                joyful_mystery_2
                Ang Pagdalaw ni Maria kay Santa Isabel
                
                ===
                
                joyful_mystery_3
                Ang Kapanganakan ni Jesus
                
                ===
                
                joyful_mystery_4
                Ang Paghahandog ni Jesus sa Templo
                
                ===
                
                joyful_mystery_5
                Ang Pagkakita ni Jesus sa Templo
                """;
    }

    private static String getDefaultLatinRosary() {
        return """
                sign_cross
                In nomine Patris, et Filii, et Spiritus Sancti. Amen.
                
                ===
                
                joyful_mystery_1
                Annuntiatio
                
                ===
                
                joyful_mystery_2
                Visitatio
                
                ===
                
                joyful_mystery_3
                Nativitas
                
                ===
                
                joyful_mystery_4
                Praesentatio
                
                ===
                
                joyful_mystery_5
                Inventio in Templo
                """;
    }

    private static String getDefaultEnglishPsalms() {
        return """
            psalm_1
            Blessed is the man that walketh not in the counsel of the ungodly,
            nor standeth in the way of sinners,
            nor sitteth in the seat of the scornful.
            But his delight is in the law of the Lord;
            and in his law doth he meditate day and night.
            And he shall be like a tree planted by the rivers of water,
            that bringeth forth his fruit in his season;
            his leaf also shall not wither;
            and whatsoever he doeth shall prosper.
            The ungodly are not so:
            but are like the chaff which the wind driveth away.
            Therefore the ungodly shall not stand in the judgment,
            nor sinners in the congregation of the righteous.
            For the Lord knoweth the way of the righteous:
            but the way of the ungodly shall perish.

            ===

            psalm_1_title
            The Way of the Righteous and the End of the Ungodly

            ===

            psalm_2
            Why do the heathen rage, and the people imagine a vain thing?
            The kings of the earth set themselves, and the rulers take counsel together,
            against the Lord, and against his anointed, saying,
            Let us break their bands asunder, and cast away their cords from us.
            He that sitteth in the heavens shall laugh: the Lord shall have them in derision.
            Then shall he speak unto them in his wrath,
            and vex them in his sore displeasure.
            Yet have I set my king upon my holy hill of Zion.
            I will declare the decree: the Lord hath said unto me,
            Thou art my Son; this day have I begotten thee.
            Ask of me, and I shall give thee the heathen for thine inheritance,
            and the uttermost parts of the earth for thy possession.
            Thou shalt break them with a rod of iron;
            thou shalt dash them in pieces like a potter’s vessel.
            Be wise now therefore, O ye kings:
            be instructed, ye judges of the earth.
            Serve the Lord with fear, and rejoice with trembling.
            Kiss the Son, lest he be angry, and ye perish from the way,
            when his wrath is kindled but a little.
            Blessed are all they that put their trust in him.

            ===

            psalm_2_title
            The Reign of the Lord’s Anointed

            ===

            psalm_3
            Lord, how are they increased that trouble me!
            many are they that rise up against me.
            Many there be which say of my soul,
            There is no help for him in God. Selah.
            But thou, O Lord, art a shield for me;
            my glory, and the lifter up of mine head.
            I cried unto the Lord with my voice,
            and he heard me out of his holy hill. Selah.
            I laid me down and slept; I awaked; for the Lord sustained me.
            I will not be afraid of ten thousands of people,
            that have set themselves against me round about.
            Arise, O Lord; save me, O my God:
            for thou hast smitten all mine enemies upon the cheek bone;
            thou hast broken the teeth of the ungodly.
            Salvation belongeth unto the Lord:
            thy blessing is upon thy people. Selah.

            ===

            psalm_3_title
            A Morning Prayer of Confidence in God

            ===

            psalm_4
            Hear me when I call, O God of my righteousness:
            thou hast enlarged me when I was in distress;
            have mercy upon me, and hear my prayer.
            O ye sons of men, how long will ye turn my glory into shame?
            how long will ye love vanity, and seek after leasing? Selah.
            But know that the Lord hath set apart him that is godly for himself:
            the Lord will hear when I call unto him.
            Stand in awe, and sin not:
            commune with your own heart upon your bed, and be still. Selah.
            Offer the sacrifices of righteousness, and put your trust in the Lord.
            There be many that say, Who will shew us any good?
            Lord, lift thou up the light of thy countenance upon us.
            Thou hast put gladness in my heart,
            more than in the time that their corn and their wine increased.
            I will both lay me down in peace, and sleep:
            for thou, Lord, only makest me dwell in safety.

            ===

            psalm_4_title
            A Prayer for Relief from Distress

            ===

            psalm_5
            Give ear to my words, O Lord,
            consider my meditation.
            Hearken unto the voice of my cry, my King, and my God:
            for unto thee will I pray.
            My voice shalt thou hear in the morning, O Lord;
            in the morning will I direct my prayer unto thee, and will look up.
            For thou art not a God that hath pleasure in wickedness:
            neither shall evil dwell with thee.
            The foolish shall not stand in thy sight:
            thou hatest all workers of iniquity.
            Thou shalt destroy them that speak leasing:
            the Lord will abhor the bloody and deceitful man.
            But as for me, I will come into thy house
            in the multitude of thy mercy:
            and in thy fear will I worship toward thy holy temple.
            Lead me, O Lord, in thy righteousness because of mine enemies;
            make thy way straight before my face.
            For there is no faithfulness in their mouth;
            their inward part is very wickedness;
            their throat is an open sepulchre;
            they flatter with their tongue.
            Destroy thou them, O God; let them fall by their own counsels;
            cast them out in the multitude of their transgressions;
            for they have rebelled against thee.
            But let all those that put their trust in thee rejoice:
            let them ever shout for joy, because thou defendest them:
            let them also that love thy name be joyful in thee.
            For thou, Lord, wilt bless the righteous;
            with favour wilt thou compass him as with a shield.

            ===

            psalm_5_title
            A Prayer for Guidance

            ===

            psalm_6
            O Lord, rebuke me not in thine anger,
            neither chasten me in thy hot displeasure.
            Have mercy upon me, O Lord; for I am weak:
            O Lord, heal me; for my bones are vexed.
            My soul is also sore vexed:
            but thou, O Lord, how long?
            Return, O Lord, deliver my soul:
            oh save me for thy mercies' sake.
            For in death there is no remembrance of thee:
            in the grave who shall give thee thanks?
            I am weary with my groaning;
            all the night make I my bed to swim;
            I water my couch with my tears.
            Mine eye is consumed because of grief;
            it waxeth old because of all mine enemies.
            Depart from me, all ye workers of iniquity;
            for the Lord hath heard the voice of my weeping.
            The Lord hath heard my supplication;
            the Lord will receive my prayer.
            Let all mine enemies be ashamed and sore vexed:
            let them return and be ashamed suddenly.

            ===

            psalm_6_title
            A Prayer for Mercy in Time of Trouble

            ===

            psalm_7
            O Lord my God, in thee do I put my trust:
            save me from all them that persecute me, and deliver me:
            Lest he tear my soul like a lion,
            rending it in pieces, while there is none to deliver.
            O Lord my God, if I have done this;
            if there be iniquity in my hands;
            If I have rewarded evil unto him that was at peace with me;
            (yea, I have delivered him that without cause is mine enemy:)
            Let the enemy persecute my soul, and take it;
            yea, let him tread down my life upon the earth,
            and lay mine honour in the dust. Selah.
            Arise, O Lord, in thine anger,
            lift up thyself because of the rage of mine enemies:
            and awake for me to the judgment that thou hast commanded.
            So shall the congregation of the people compass thee about:
            for their sakes therefore return thou on high.
            The Lord shall judge the people:
            judge me, O Lord, according to my righteousness,
            and according to mine integrity that is in me.
            Oh let the wickedness of the wicked come to an end;
            but establish the just:
            for the righteous God trieth the hearts and reins.
            My defence is of God,
            which saveth the upright in heart.
            God judgeth the righteous,
            and God is angry with the wicked every day.
            If he turn not, he will whet his sword;
            he hath bent his bow, and made it ready.
            He hath also prepared for him the instruments of death;
            he ordaineth his arrows against the persecutors.
            Behold, he travaileth with iniquity,
            and hath conceived mischief, and brought forth falsehood.
            He made a pit, and digged it,
            and is fallen into the ditch which he made.
            His mischief shall return upon his own head,
            and his violent dealing shall come down upon his own pate.
            I will praise the Lord according to his righteousness:
            and will sing praise to the name of the Lord most high.

            ===

            psalm_7_title
            A Prayer for Justice against Persecutors

            ===

            psalm_8
            O Lord, our Lord, how excellent is thy name in all the earth!
            who hast set thy glory above the heavens.
            Out of the mouth of babes and sucklings hast thou ordained strength
            because of thine enemies,
            that thou mightest still the enemy and the avenger.
            When I consider thy heavens, the work of thy fingers,
            the moon and the stars, which thou hast ordained;
            What is man, that thou art mindful of him?
            and the son of man, that thou visitest him?
            For thou hast made him a little lower than the angels,
            and hast crowned him with glory and honour.
            Thou madest him to have dominion over the works of thy hands;
            thou hast put all things under his feet:
            All sheep and oxen, yea, and the beasts of the field;
            The fowl of the air, and the fish of the sea,
            and whatsoever passeth through the paths of the seas.
            O Lord our Lord, how excellent is thy name in all the earth!

            ===

            psalm_8_title
            The Glory of the Lord in Creation

            ===

            psalm_9
            I will praise thee, O Lord, with my whole heart;
            I will shew forth all thy marvellous works.
            I will be glad and rejoice in thee:
            I will sing praise to thy name, O thou most High.
            When mine enemies are turned back,
            they shall fall and perish at thy presence.
            For thou hast maintained my right and my cause;
            thou satest in the throne judging right.
            Thou hast rebuked the heathen, thou hast destroyed the wicked,
            thou hast put out their name for ever and ever.
            O thou enemy, destructions are come to a perpetual end:
            and thou hast destroyed cities; their memorial is perished with them.
            But the Lord shall endure for ever:
            he hath prepared his throne for judgment.
            And he shall judge the world in righteousness,
            he shall minister judgment to the people in uprightness.
            The Lord also will be a refuge for the oppressed,
            a refuge in times of trouble.
            And they that know thy name will put their trust in thee:
            for thou, Lord, hast not forsaken them that seek thee.
            Sing praises to the Lord, which dwelleth in Zion:
            declare among the people his doings.
            When he maketh inquisition for blood, he remembereth them:
            he forgetteth not the cry of the humble.
            Have mercy upon me, O Lord;
            consider my trouble which I suffer of them that hate me,
            thou that liftest me up from the gates of death:
            That I may shew forth all thy praise in the gates of the daughter of Zion:
            I will rejoice in thy salvation.
            The heathen are sunk down in the pit that they made:
            in the net which they hid is their own foot taken.
            The Lord is known by the judgment which he executeth:
            the wicked is snared in the work of his own hands. Higgaion. Selah.
            The wicked shall be turned into hell,
            and all the nations that forget God.
            For the needy shall not always be forgotten:
            the expectation of the poor shall not perish for ever.
            Arise, O Lord; let not man prevail:
            let the heathen be judged in thy sight.
            Put them in fear, O Lord:
            that the nations may know themselves to be but men. Selah.

            ===

            psalm_9_title
            Thanksgiving for God’s Justice

            ===

            psalm_10
            Why standest thou afar off, O Lord?
            why hidest thou thyself in times of trouble?
            The wicked in his pride doth persecute the poor:
            let them be taken in the devices that they have imagined.
            For the wicked boasteth of his heart’s desire,
            and blesseth the covetous, whom the Lord abhorreth.
            The wicked, through the pride of his countenance, will not seek after God:
            God is not in all his thoughts.
            His ways are always grievous;
            thy judgments are far above out of his sight:
            as for all his enemies, he puffeth at them.
            He hath said in his heart, I shall not be moved:
            for I shall never be in adversity.
            His mouth is full of cursing and deceit and fraud:
            under his tongue is mischief and vanity.
            He sitteth in the lurking places of the villages:
            in the secret places doth he murder the innocent:
            his eyes are privily set against the poor.
            He lieth in wait secretly as a lion in his den:
            he lieth in wait to catch the poor:
            he doth catch the poor, when he draweth him into his net.
            He croucheth, and humbleth himself,
            that the poor may fall by his strong ones.
            He hath said in his heart, God hath forgotten:
            he hideth his face; he will never see it.
            Arise, O Lord; O God, lift up thine hand:
            forget not the humble.
            Wherefore doth the wicked contemn God?
            he hath said in his heart, Thou wilt not require it.
            Thou hast seen it; for thou beholdest mischief and spite,
            to requite it with thy hand:
            the poor committeth himself unto thee;
            thou art the helper of the fatherless.
            Break thou the arm of the wicked and the evil man:
            seek out his wickedness till thou find none.
            The Lord is King for ever and ever:
            the heathen are perished out of his land.
            Lord, thou hast heard the desire of the humble:
            thou wilt prepare their heart, thou wilt cause thine ear to hear:
            To judge the fatherless and the oppressed,
            that the man of the earth may no more oppress.

            ===

            psalm_10_title
            A Plea for God’s Justice

            ===

            psalm_23
            The Lord is my shepherd; I shall not want.
            He maketh me to lie down in green pastures:
            he leadeth me beside the still waters.
            He restoreth my soul:
            he leadeth me in the paths of righteousness for his name's sake.
            Yea, though I walk through the valley of the shadow of death,
            I will fear no evil: for thou art with me;
            thy rod and thy staff they comfort me.
            Thou preparest a table before me in the presence of mine enemies:
            thou anointest my head with oil; my cup runneth over.
            Surely goodness and mercy shall follow me all the days of my life:
            and I will dwell in the house of the Lord for ever.

            ===

            psalm_23_title
            The Lord is My Shepherd

            ===

            psalm_51
            Have mercy upon me, O God, according to thy lovingkindness:
            according unto the multitude of thy tender mercies blot out my transgressions.
            Wash me throughly from mine iniquity, and cleanse me from my sin.
            For I acknowledge my transgressions: and my sin is ever before me.
            Create in me a clean heart, O God; and renew a right spirit within me.
            Cast me not away from thy presence; and take not thy holy spirit from me.
            Restore unto me the joy of thy salvation; and uphold me with thy free spirit.

            ===

            psalm_51_title
            Create in Me a Clean Heart
            """;
    }


    /**
     * Save content to a file
     */
    public static boolean saveToFile(String content, String filePath) {
        try {
            Path path = Paths.get(filePath);
            // Create directories if they don't exist
            Files.createDirectories(path.getParent());
            Files.writeString(path, content, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving file: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if a file exists
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
}