package nin.spiritualism.utils;

import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.Style;

public class ChatUtils {
    public static final MessageSignature spiritSig = new MessageSignature(new byte[]{11, 45, 14});
    public static final GuiMessageTag spiritTag = new GuiMessageTag(0xe2eff5, null, null, "Spiritualism");
    public static final Style spiritStyle = Style.EMPTY.withColor(0x919da3).withItalic(true);
    public static ChatComponent cc = Minecraft.getInstance().gui.getChat();

    public static Component refused(String name) {
        return getSpiritComponent(name + "の霊的な力により憑依を阻害されました");
    }

    public static Component possessed(String name) {
        return getSpiritComponent(name + "に憑依されました");
    }

    public static Component left(String name) {
        return getSpiritComponent(name + "が去りました");
    }

    public static Component controlling(int i) {
        return getSpiritComponent("残り" + i / 20 + "秒間の集中");
    }

    public static Component interrupted() {
        return getSpiritComponent("精神が乱れました");
    }

    public static Component controlled() {
        return getSpiritComponent("霊力を制御できました");
    }

    public static Component soulUsage(int i) {
        return getSpiritComponent("現在の霊力:" + i);
    }

    public static Component refusePossession(boolean b) {
        return getSpiritComponent(b ? "憑依拒否中" : "憑依許容中");
    }

    private static Component getSpiritComponent(String s) {
        return Component.literal(s).withStyle(spiritStyle);
    }

    public static void showComponent(Component c) {
        cc.deleteMessage(spiritSig);
        cc.addMessage(c, spiritSig, spiritTag);
    }
}
