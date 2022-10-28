package nin.spiritualism.utils;

import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.Style;

public class ChatUtils {
    public static ChatComponent cc = Minecraft.getInstance().gui.getChat();

    public static final MessageSignature spiritSig = new MessageSignature(new byte[]{11, 45, 14});
    public static final GuiMessageTag spiritTag = new GuiMessageTag(0xe2eff5, null, null, "hyoui");
    public static final Style spiritStyle = Style.EMPTY.withColor(0x919da3).withItalic(true);

    public static void showRefused(String name) {
        cc.deleteMessage(spiritSig);
        cc.addMessage(Component.literal(name + "の霊的な力により憑依を阻害されました").withStyle(spiritStyle), spiritSig, spiritTag);
    }

    public static void showPossessed(Component c) {
        cc.deleteMessage(spiritSig);
        cc.addMessage(Component.literal(c.getString() + "に憑依されました").withStyle(spiritStyle), spiritSig, spiritTag);
    }

    public static void showLeft(Component c) {
        cc.deleteMessage(spiritSig);
        cc.addMessage(Component.literal(c.getString() + "が去りました").withStyle(spiritStyle), spiritSig, spiritTag);
    }
}
