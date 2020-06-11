package me.swirtzly.regeneration.client.skinhandling;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import me.swirtzly.regeneration.RegenConfig;
import me.swirtzly.regeneration.Regeneration;
import me.swirtzly.regeneration.client.rendering.types.ATypeRenderer;
import me.swirtzly.regeneration.common.capability.IRegen;
import me.swirtzly.regeneration.common.capability.RegenCap;
import me.swirtzly.regeneration.common.skin.HandleSkins;
import me.swirtzly.regeneration.common.types.RegenType;
import me.swirtzly.regeneration.network.NetworkDispatcher;
import me.swirtzly.regeneration.network.messages.UpdateSkinMessage;
import me.swirtzly.regeneration.util.client.ClientUtil;
import me.swirtzly.regeneration.util.client.TexUtil;
import me.swirtzly.regeneration.util.common.PlayerUtil;
import me.swirtzly.regeneration.util.common.RegenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static me.swirtzly.regeneration.util.common.RegenUtil.NO_SKIN;

@OnlyIn(Dist.CLIENT)
public class SkinManipulation {

	public static final File SKIN_DIRECTORY = new File(RegenConfig.CLIENT.skinDir.get() + "/Regeneration Data/skins/");
	public static final Map<UUID, SkinInfo> PLAYER_SKINS = new HashMap<>();
	public static final File SKIN_DIRECTORY_STEVE = new File(SKIN_DIRECTORY, "/steve");
	public static final File SKIN_DIRECTORY_ALEX = new File(SKIN_DIRECTORY, "/alex");
	private static final HashMap<UUID, ResourceLocation> MOJANG = new HashMap<>();
	private static final Random RAND = new Random();


	public static NativeImage decodeToImage(String base64String) {
		if (base64String.equalsIgnoreCase(NO_SKIN)) {
			return null;
		}
		try {
			return NativeImage.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64String.replaceAll("-", ""))));
		} catch (final IOException ioe) {
			Regeneration.LOG.error("ERROR MAKING IMAGE FOR: " + base64String);
			throw new UncheckedIOException(ioe);
		}
	}

	/**
	 * Choosens a random png file from Steve/Alex Directory (This really depends on the Clients preference) It also checks image size of the select file, if it's too large, we'll just reset the player back to their Mojang skin, else they will be kicked from their server. If the player has disabled skin changing on the client, it will just send a reset packet
	 *
	 * @param random - This kinda explains itself, doesn't it?
	 * @param player - Player instance, used to check UUID to ensure it is the client player being involved in the scenario
	 * @throws IOException
	 */

	public static void sendSkinUpdate(Random random, PlayerEntity player) {
		if (Minecraft.getInstance().player.getUniqueID() != player.getUniqueID()) return;
		RegenCap.get(player).ifPresent((data) -> {

			if (RegenConfig.CLIENT.changeMySkin.get()) {

				String pixelData = NO_SKIN;
				File skin = null;

				if (data.getNextSkin().equals(NO_SKIN)) {
					boolean isAlex = data.getPreferredModel().isAlex();
					skin = SkinManipulation.chooseRandomSkin(random, isAlex);
					Regeneration.LOG.info(skin + " was selected");
					pixelData = HandleSkins.imageToPixelData(skin);
					data.setEncodedSkin(pixelData);
					NetworkDispatcher.sendToServer(new UpdateSkinMessage(pixelData, isAlex));
				} else {
					pixelData = data.getNextSkin();
					data.setEncodedSkin(pixelData);
					NetworkDispatcher.sendToServer(new UpdateSkinMessage(pixelData, data.getNextSkinType().getMojangType().equals("slim")));
				}
			} else {
				ClientUtil.sendSkinResetPacket();
			}
		});
	}


	private static File chooseRandomSkin(Random rand, boolean isAlex) {
		File skins = isAlex ? SKIN_DIRECTORY_ALEX : SKIN_DIRECTORY_STEVE;
		Collection<File> folderFiles = FileUtils.listFiles(skins, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		if (folderFiles.isEmpty()) {
			folderFiles = FileUtils.listFiles(skins, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		}
		return (File) folderFiles.toArray()[rand.nextInt(folderFiles.size())];
	}

	private static SkinInfo getSkinInfo(AbstractClientPlayerEntity player, IRegen data) {
		ResourceLocation resourceLocation;
		SkinInfo.SkinType skinType = SkinInfo.SkinType.ALEX;
		if (data == null) {
			return new SkinInfo(player, null, getSkinType(player, true));
		}
		if (data.getEncodedSkin().equals(NO_SKIN)) {
			try {
				resourceLocation = TexUtil.urlToTexture(new URL("https://crafatar.com/skins/" + player.getUniqueID()));//MOJANG.get(player.getUniqueID());
			} catch (MalformedURLException e) {
				resourceLocation = MOJANG.get(player.getUniqueID());
			}
			skinType = getSkinType(player, true);
		} else {
			NativeImage nativeImage = decodeToImage(data.getEncodedSkin());
			if (nativeImage == null) {
				resourceLocation = DefaultPlayerSkin.getDefaultSkin(player.getUniqueID());
			} else {
				DynamicTexture tex = new DynamicTexture(nativeImage);
				resourceLocation = Minecraft.getInstance().getTextureManager().getDynamicTextureLocation(player.getName().getUnformattedComponentText().toLowerCase() + "_skin_" + System.currentTimeMillis(), tex);
				skinType = data.getSkinType();
			}
		}
		return new SkinInfo(player, resourceLocation, skinType);
	}


	public static SkinInfo.SkinType getSkinType(PlayerEntity player, boolean forceMojang) {
		Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Minecraft.getInstance().getSkinManager().loadSkinFromCache(player.getGameProfile());
		if (map.isEmpty()) {
			map = Minecraft.getInstance().getSessionService().getTextures(Minecraft.getInstance().getSessionService().fillProfileProperties(player.getGameProfile(), false), false);
		}
		MinecraftProfileTexture profile = map.get(MinecraftProfileTexture.Type.SKIN);

		AtomicReference<SkinInfo.SkinType> skinType = new AtomicReference<>();
		skinType.set(SkinInfo.SkinType.ALEX);

		RegenCap.get(player).ifPresent((data) -> {

			if (data.getEncodedSkin().toLowerCase().equals("none") || forceMojang) {
				if (profile == null) {
					skinType.set(SkinInfo.SkinType.STEVE);
				}
				if (profile != null && profile.getMetadata("model") == null) {
					skinType.set(SkinInfo.SkinType.STEVE);
				}
			} else {
				skinType.set(data.getSkinType());
			}
		});

		return skinType.get();
	}


	public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getVanillaMap(AbstractClientPlayerEntity player) {
		Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Minecraft.getInstance().getSkinManager().loadSkinFromCache(player.getGameProfile());
		if (map.isEmpty()) {
			map = Minecraft.getInstance().getSessionService().getTextures(Minecraft.getInstance().getSessionService().fillProfileProperties(player.getGameProfile(), false), false);
		}
		return map;
	}

	public static void setPlayerSkin(AbstractClientPlayerEntity player, ResourceLocation texture) {
		if (player.getLocationSkin() == texture) {
			return;
		}
		NetworkPlayerInfo playerInfo = player.playerInfo;
		if (playerInfo == null) return;
		Map<MinecraftProfileTexture.Type, ResourceLocation> playerTextures = playerInfo.playerTextures;
		playerTextures.put(MinecraftProfileTexture.Type.SKIN, texture);
		if (texture == null) {
			ObfuscationReflectionHelper.setPrivateValue(NetworkPlayerInfo.class, playerInfo, false, "playerTexturesLoaded");
		}
	}

	public static void setPlayerSkinType(AbstractClientPlayerEntity player, SkinInfo.SkinType skinType) {
		if (skinType.getMojangType().equals(player.getSkinType())) return;
		NetworkPlayerInfo playerInfo = player.playerInfo;
		if (playerInfo == null) return;
		ObfuscationReflectionHelper.setPrivateValue(NetworkPlayerInfo.class, playerInfo, skinType.getMojangType(), "skinType");
	}


	public static List<File> listAllSkins(EnumChoices choices) {
		List<File> resultList = new ArrayList<>();
		File directory = null;

		switch (choices) {
			case EITHER:
				directory = SKIN_DIRECTORY;
				break;
			case ALEX:
				directory = SKIN_DIRECTORY_ALEX;
				break;
			case STEVE:
				directory = SKIN_DIRECTORY_STEVE;
				break;
		}
		try {
			Files.find(Paths.get(directory.toString()), Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile()).forEach((file) -> resultList.add(file.toFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultList;
	}

	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Pre renderPlayerEvent) {
		AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) renderPlayerEvent.getPlayer();

		//TODO: THIS SHOULD NOT EXIST AND NEEDS TO BE REPLACED AS SOON AS POSSIBLE
		if (!MOJANG.containsKey(player.getUniqueID())) {
			MOJANG.put(player.getUniqueID(), player.getLocationSkin());
		}

		RegenCap.get(player).ifPresent((cap) -> {

			/* When the player is in a Post Regenerative state and above a 3x3 grid of Zero Rounde Blocks,
			 *  We want them to float up and down slightly*/
			if (cap.getState() == PlayerUtil.RegenState.POST && PlayerUtil.isAboveZeroGrid(player)) {
				float floatingOffset = MathHelper.cos(player.ticksExisted * 0.1F) * -0.09F + 0.5F;
				GlStateManager.translated(0, floatingOffset, 0);
			}

			/* Sometimes when the player is teleported, the Mojang skin becomes re-downloaded and resets to either Steve,
			 or the Mojang Skin, so once they have been re-created, we remove the cache we have on them, causing it to be renewed */
			if (player.ticksExisted == 20) {
				PLAYER_SKINS.remove(player.getUniqueID());
			}

			/* 	When the player regenerates, we want the skin to change midway through Regeneration
			 *	We only do this midway through, we will destroy the data and re-create it */
			boolean isMidRegeneration = cap.getState() == PlayerUtil.RegenState.REGENERATING && cap.getAnimationTicks() >= 100;
			if (isMidRegeneration) {
				createSkinData(player, RegenCap.get(player));
			}

			/* Render the living entities Pre-Regeneration effect */
			if (cap.getState() == PlayerUtil.RegenState.REGENERATING) {
				RegenType typeInstance = cap.getType().create();
				ATypeRenderer typeRenderer = typeInstance.getRenderer();
				typeRenderer.onRenderRegeneratingPlayerPre(typeInstance, renderPlayerEvent, cap);
			}



			/* Grab the SkinInfo of a player and set their SkinType and Skin location from it */
			SkinInfo skin = PLAYER_SKINS.get(player.getUniqueID());
			if (skin != null) {
				setPlayerSkin(player, skin.getTextureLocation());
				setPlayerSkinType(player, skin.getSkintype());
			} else {
				createSkinData(player, RegenCap.get(player));
			}

		});
	}

	@SubscribeEvent
	public void onRenderPlayer(RenderPlayerEvent.Post renderPlayerEventPost) {
		AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) renderPlayerEventPost.getPlayer();
		RegenCap.get(player).ifPresent((cap) -> {
			if (cap.getState() == PlayerUtil.RegenState.REGENERATING) {
				RegenType type = cap.getType().create();
				ATypeRenderer typeRenderer = type.getRenderer();
				typeRenderer.onRenderRegeneratingPlayerPost(type, renderPlayerEventPost, cap);
			}
		});
	}

	private void createSkinData(AbstractClientPlayerEntity player, LazyOptional<IRegen> cap) {
		cap.ifPresent((data) -> {
			SkinInfo skinInfo = SkinManipulation.getSkinInfo(player, data);

			/* Set player skin and SkinType and cache it so we don't keep re-making it */
			SkinManipulation.setPlayerSkin(player, skinInfo.getTextureLocation());
			SkinManipulation.setPlayerSkinType(player, skinInfo.getSkintype());
			PLAYER_SKINS.put(player.getGameProfile().getId(), skinInfo);
		});
	}

	public enum EnumChoices implements RegenUtil.IEnum {
		ALEX(true), STEVE(false), EITHER(true);

		private boolean isAlex;

		EnumChoices(boolean isAlex) {
			this.isAlex = isAlex;
		}

		public boolean isAlex() {
			if (this == EITHER) {
				return RAND.nextBoolean();
			}
			return isAlex;
		}
	}

}
