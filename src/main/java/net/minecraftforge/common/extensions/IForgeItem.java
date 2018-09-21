/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common.extensions;

import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistrySimple;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

// TODO review most of the methods in this "patch"
public interface IForgeItem
{
    // Helpers for accessing Item data
    default Item getItem()
    {
        return (Item) this;
    }

    /**
     * ItemStack sensitive version of getItemAttributeModifiers
     */
    @SuppressWarnings("deprecation")
    default Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
    {
        return getItem().getItemAttributeModifiers(slot);
    }

    /**
     * Called when a player drops the item into the world, returning false from this
     * will prevent the item from being removed from the players inventory and
     * spawning in the world
     *
     * @param player The player that dropped the item
     * @param item   The item stack, before the item is removed.
     */
    default boolean onDroppedByPlayer(ItemStack item, EntityPlayer player)
    {
        return true;
    }

    /**
     * Allow the item one last chance to modify its name used for the tool highlight
     * useful for adding something extra that can't be removed by a user in the
     * displayed name, such as a mode of operation.
     *
     * @param item        the ItemStack for the item.
     * @param displayName the name that will be displayed unless it is changed in
     *                    this method.
     */
    default String getHighlightTip(ItemStack item, String displayName)
    {
        return displayName;
    }

    /**
     * This is called when the item is used, before the block is activated.
     *
     * @return Return PASS to allow vanilla handling, any other to skip normal code.
     */
    default EnumActionResult onItemUseFirst(ItemUseContext context)
    {
        return EnumActionResult.PASS;
    }

    /**
     * Called by CraftingManager to determine if an item is reparable.
     *
     * @return True if reparable
     */
    boolean isRepairable();

    /**
     * Override this method to change the NBT data being sent to the client. You
     * should ONLY override this when you have no other choice, as this might change
     * behavior client side!
     *
     * Note that this will sometimes be applied multiple times, the following MUST
     * be supported: Item item = stack.getItem(); NBTTagCompound nbtShare1 =
     * item.getNBTShareTag(stack); stack.setTagCompound(nbtShare1); NBTTagCompound
     * nbtShare2 = item.getNBTShareTag(stack); assert nbtShare1.equals(nbtShare2);
     *
     * @param stack The stack to send the NBT tag for
     * @return The NBT tag
     */
    @Nullable
    default NBTTagCompound getNBTShareTag(ItemStack stack)
    {
        return stack.getTagCompound();
    }

    /**
     * Override this method to decide what to do with the NBT data received from
     * getNBTShareTag().
     *
     * @param stack The stack that received NBT
     * @param nbt   Received NBT, can be null
     */
    default void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        stack.setTagCompound(nbt);
    }

    /**
     * Called before a block is broken. Return true to prevent default block
     * harvesting.
     *
     * Note: In SMP, this is called on both client and server sides!
     *
     * @param itemstack The current ItemStack
     * @param pos       Block's position in world
     * @param player    The Player that is wielding the item
     * @return True to prevent harvesting, false to continue as normal
     */
    default boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player)
    {
        return false;
    }

    /**
     * Called each tick while using an item.
     *
     * @param stack  The Item being used
     * @param player The Player using the item
     * @param count  The amount of time in tick the item has been used for
     *               continuously
     */
    default void onUsingTick(ItemStack stack, EntityLivingBase player, int count)
    {
    }

    /**
     * Called when the player Left Clicks (attacks) an entity. Processed before
     * damage is done, if return value is true further processing is canceled and
     * the entity is not attacked.
     *
     * @param stack  The Item being used
     * @param player The player that is attacking
     * @param entity The entity being attacked
     * @return True to cancel the rest of the interaction.
     */
    default boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        return false;
    }

    /**
     * ItemStack sensitive version of getContainerItem. Returns a full ItemStack
     * instance of the result.
     *
     * @param itemStack The current ItemStack
     * @return The resulting ItemStack
     */
    default ItemStack getContainerItem(ItemStack itemStack)
    {
        if (!hasContainerItem(itemStack))
        {
            return ItemStack.EMPTY;
        }
        return new ItemStack(getItem().getContainerItem());
    }

    /**
     * ItemStack sensitive version of hasContainerItem
     *
     * @param stack The current item stack
     * @return True if this item has a 'container'
     */
    @SuppressWarnings("deprecation")
    default boolean hasContainerItem(ItemStack stack)
    {
        return getItem().hasContainerItem();
    }

    /**
     * Retrieves the normal 'lifespan' of this item when it is dropped on the ground
     * as a EntityItem. This is in ticks, standard result is 6000, or 5 mins.
     *
     * @param itemStack The current ItemStack
     * @param world     The world the entity is in
     * @return The normal lifespan in ticks.
     */
    default int getEntityLifespan(ItemStack itemStack, World world)
    {
        return 6000;
    }

    /**
     * Determines if this Item has a special entity for when they are in the world.
     * Is called when a EntityItem is spawned in the world, if true and
     * Item#createCustomEntity returns non null, the EntityItem will be destroyed
     * and the new Entity will be added to the world.
     *
     * @param stack The current item stack
     * @return True of the item has a custom entity, If true,
     *         Item#createCustomEntity will be called
     */
    default boolean hasCustomEntity(ItemStack stack)
    {
        return false;
    }

    /**
     * This function should return a new entity to replace the dropped item.
     * Returning null here will not kill the EntityItem and will leave it to
     * function normally. Called when the item it placed in a world.
     *
     * @param world     The world object
     * @param location  The EntityItem object, useful for getting the position of
     *                  the entity
     * @param itemstack The current item stack
     * @return A new Entity object to spawn or null
     */
    @Nullable
    default Entity createEntity(World world, Entity location, ItemStack itemstack)
    {
        return null;
    }

    /**
     * Called by the default implemetation of EntityItem's onUpdate method, allowing
     * for cleaner control over the update of the item without having to write a
     * subclass.
     *
     * @param entityItem The entity Item
     * @return Return true to skip any further update code.
     */
    default boolean onEntityItemUpdate(net.minecraft.entity.item.EntityItem entityItem)
    {
        return false;
    }

    /**
     * Gets a list of tabs that items belonging to this class can display on,
     * combined properly with getSubItems allows for a single item to span many
     * sub-items across many tabs.
     *
     * @return A list of all tabs that this item could possibly be one.
     */
    default java.util.Collection<ItemGroup> getCreativeTabs()
    {
        return java.util.Collections.singletonList(getItem().getCreativeTab());
    }

    /**
     * Determines the base experience for a player when they remove this item from a
     * furnace slot. This number must be between 0 and 1 for it to be valid. This
     * number will be multiplied by the stack size to get the total experience.
     *
     * @param item The item stack the player is picking up.
     * @return The amount to award for each item.
     */
    default float getSmeltingExperience(ItemStack item)
    {
        return -1; // -1 will default to the old lookups.
    }

    /**
     *
     * Should this item, when held, allow sneak-clicks to pass through to the
     * underlying block?
     *
     * @param world  The world
     * @param pos    Block position in world
     * @param player The Player that is wielding the item
     * @return
     */
    default boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.IWorldReader world, BlockPos pos, EntityPlayer player)
    {
        return false;
    }

    /**
     * Called to tick armor in the armor slot. Override to do something
     */
    default void onArmorTick(World world, EntityPlayer player, ItemStack itemStack)
    {
    }

    /**
     * Determines if the specific ItemStack can be placed in the specified armor
     * slot, for the entity.
     *
     * TODO: Change name to canEquip in 1.13?
     *
     * @param stack     The ItemStack
     * @param armorType Armor slot to be verified.
     * @param entity    The entity trying to equip the armor
     * @return True if the given ItemStack can be inserted in the slot
     */
    default boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity)
    {
        return net.minecraft.entity.EntityLiving.getSlotForItemStack(stack) == armorType;
    }

    /**
     * Override this to set a non-default armor slot for an ItemStack, but <em>do
     * not use this to get the armor slot of said stack; for that, use
     * {@link net.minecraft.entity.EntityLiving#getSlotForItemStack(ItemStack)}.</em>
     *
     * @param stack the ItemStack
     * @return the armor slot of the ItemStack, or {@code null} to let the default
     *         vanilla logic as per {@code EntityLiving.getSlotForItemStack(stack)}
     *         decide
     */
    @Nullable
    default EntityEquipmentSlot getEquipmentSlot(ItemStack stack)
    {
        return null;
    }

    /**
     * Allow or forbid the specific book/item combination as an anvil enchant
     *
     * @param stack The item
     * @param book  The book
     * @return if the enchantment is allowed
     */
    default boolean isBookEnchantable(ItemStack stack, ItemStack book)
    {
        return true;
    }

    /**
     * Called by RenderBiped and RenderPlayer to determine the armor texture that
     * should be use for the currently equipped item. This will only be called on
     * instances of ItemArmor.
     *
     * Returning null from this function will use the default value.
     *
     * @param stack  ItemStack for the equipped armor
     * @param entity The entity wearing the armor
     * @param slot   The slot the armor is in
     * @param type   The subtype, can be null or "overlay"
     * @return Path of texture to bind, or null to use default
     */
    @Nullable
    default String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
    {
        return null;
    }

    /**
     * Returns the font renderer used to render tooltips and overlays for this item.
     * Returning null will use the standard font renderer.
     *
     * @param stack The current item stack
     * @return A instance of FontRenderer or null to use default
     */
    @OnlyIn(Dist.CLIENT)
    @Nullable
    default net.minecraft.client.gui.FontRenderer getFontRenderer(ItemStack stack)
    {
        return null;
    }

    /**
     * Override this method to have an item handle its own armor rendering.
     *
     * @param entityLiving The entity wearing the armor
     * @param itemStack    The itemStack to render the model of
     * @param armorSlot    The slot the armor is in
     * @param _default     Original armor model. Will have attributes set.
     * @return A ModelBiped to render instead of the default
     */
    @OnlyIn(Dist.CLIENT)
    @Nullable
    default net.minecraft.client.renderer.entity.model.ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack,
            EntityEquipmentSlot armorSlot, net.minecraft.client.renderer.entity.model.ModelBiped _default)
    {
        return null;
    }

    /**
     * Called when a entity tries to play the 'swing' animation.
     *
     * @param entityLiving The entity swinging the item.
     * @param stack        The Item stack
     * @return True to cancel any further processing by EntityLiving
     */
    default boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
        return false;
    }

    /**
     * Called when the client starts rendering the HUD, for whatever item the player
     * currently has as a helmet. This is where pumpkins would render there overlay.
     *
     * @param stack        The ItemStack that is equipped
     * @param player       Reference to the current client entity
     * @param resolution   Resolution information about the current viewport and
     *                     configured GUI Scale
     * @param partialTicks Partial ticks for the renderer, useful for interpolation
     */
    @OnlyIn(Dist.CLIENT)
    default void renderHelmetOverlay(ItemStack stack, EntityPlayer player, int width, int height, float partialTicks)
    {
    }

    /**
     * Return the itemDamage represented by this ItemStack. Defaults to the Damage
     * entry in the stack NBT, but can be overridden here for other sources.
     *
     * @param stack The itemstack that is damaged
     * @return the damage value
     */
    default int getDamage(ItemStack stack)
    {
        return !stack.hasTagCompound() ? 0 : stack.getTagCompound().getInteger("Damage");
    }

    /**
     * Determines if the durability bar should be rendered for this item. Defaults
     * to vanilla stack.isDamaged behavior. But modders can use this for any data
     * they wish.
     *
     * @param stack The current Item Stack
     * @return True if it should render the 'durability' bar.
     */
    default boolean showDurabilityBar(ItemStack stack)
    {
        return stack.isItemDamaged();
    }

    /**
     * Queries the percentage of the 'Durability' bar that should be drawn.
     *
     * @param stack The current ItemStack
     * @return 0.0 for 100% (no damage / full bar), 1.0 for 0% (fully damaged /
     *         empty bar)
     */
    default double getDurabilityForDisplay(ItemStack stack)
    {
        return (double) stack.getItemDamage() / (double) stack.getMaxDamage();
    }

    /**
     * Returns the packed int RGB value used to render the durability bar in the
     * GUI. Defaults to a value based on the hue scaled based on
     * {@link #getDurabilityForDisplay}, but can be overriden.
     *
     * @param stack Stack to get durability from
     * @return A packed RGB value for the durability colour (0x00RRGGBB)
     */
    default int getRGBDurabilityForDisplay(ItemStack stack)
    {
        return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - getDurabilityForDisplay(stack))) / 3.0F, 1.0F, 1.0F);
    }

    /**
     * Return the maxDamage for this ItemStack. Defaults to the maxDamage field in
     * this item, but can be overridden here for other sources such as NBT.
     *
     * @param stack The itemstack that is damaged
     * @return the damage value
     */
    @SuppressWarnings("deprecation")
    default int getMaxDamage(ItemStack stack)
    {
        return getItem().getMaxDamage();
    }

    /**
     * Return if this itemstack is damaged. Note only called if
     * {@link #isDamageable()} is true.
     *
     * @param stack the stack
     * @return if the stack is damaged
     */
    default boolean isDamaged(ItemStack stack)
    {
        return stack.getItemDamage() > 0;
    }

    /**
     * Set the damage for this itemstack. Note, this method is responsible for zero
     * checking.
     *
     * @param stack  the stack
     * @param damage the new damage value
     */
    default void setDamage(ItemStack stack, int damage)
    {
        stack.func_196082_o().setInteger("Damage", Math.max(0, damage));
    }

    /**
     * Checked from
     * {@link net.minecraft.client.multiplayer.PlayerControllerMP#onPlayerDestroyBlock(BlockPos pos)
     * PlayerControllerMP.onPlayerDestroyBlock()} when a creative player left-clicks
     * a block with this item. Also checked from
     * {@link net.minecraftforge.common.ForgeHooks#onBlockBreakEvent(World, GameType, EntityPlayerMP, BlockPos)
     * ForgeHooks.onBlockBreakEvent()} to prevent sending an event.
     *
     * @return true if the given player can destroy specified block in creative mode
     *         with this item
     */
    default boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player)
    {
        return !(this instanceof ItemSword);
    }

    /**
     * ItemStack sensitive version of {@link #canHarvestBlock(IBlockState)}
     *
     * @param stack The itemstack used to harvest the block
     * @param state The block trying to harvest
     * @return true if can harvest the block
     */
    default boolean canHarvestBlock(ItemStack stack, IBlockState state)
    {
        return getItem().canHarvestBlock(state);
    }

    /**
     * Gets the maximum number of items that this stack should be able to hold. This
     * is a ItemStack (and thus NBT) sensitive version of Item.getItemStackLimit()
     *
     * @param stack The ItemStack
     * @return The maximum number this item can be stacked to
     */
    @SuppressWarnings("deprecation")
    default int getItemStackLimit(ItemStack stack)
    {
        return getItem().getItemStackLimit();
    }

    Set<ToolType> getToolTypes(ItemStack stack);

    /**
     * Queries the harvest level of this item stack for the specified tool class,
     * Returns -1 if this tool is not of the specified type
     *
     * @param stack      This item stack instance
     * @param toolClass  Tool Class
     * @param player     The player trying to harvest the given blockstate
     * @param blockState The block to harvest
     * @return Harvest level, or -1 if not the specified tool type.
     */
    int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable EntityPlayer player, @Nullable IBlockState blockState);

    /**
     * ItemStack sensitive version of getItemEnchantability
     *
     * @param stack The ItemStack
     * @return the item echantability value
     */
    default int getItemEnchantability(ItemStack stack)
    {
        return getItem().getItemEnchantability();
    }

    /**
     * Checks whether an item can be enchanted with a certain enchantment. This
     * applies specifically to enchanting an item in the enchanting table and is
     * called when retrieving the list of possible enchantments for an item.
     * Enchantments may additionally (or exclusively) be doing their own checks in
     * {@link net.minecraft.enchantment.Enchantment#canApplyAtEnchantingTable(ItemStack)};
     * check the individual implementation for reference. By default this will check
     * if the enchantment type is valid for this item type.
     *
     * @param stack       the item stack to be enchanted
     * @param enchantment the enchantment to be applied
     * @return true if the enchantment can be applied to this item
     */
    default boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment)
    {
        return enchantment.type.canEnchantItem(stack.getItem());
    }

    /**
     * Whether this Item can be used as a payment to activate the vanilla beacon.
     *
     * @param stack the ItemStack
     * @return true if this Item can be used
     */
    default boolean isBeaconPayment(ItemStack stack)
    {
        return this == Items.EMERALD || this == Items.DIAMOND || this == Items.GOLD_INGOT || this == Items.IRON_INGOT;
    }

    /**
     * Determine if the player switching between these two item stacks
     *
     * @param oldStack    The old stack that was equipped
     * @param newStack    The new stack
     * @param slotChanged If the current equipped slot was changed, Vanilla does not
     *                    play the animation if you switch between two slots that
     *                    hold the exact same item.
     * @return True to play the item change animation
     */
    default boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return !oldStack.equals(newStack); // !ItemStack.areItemStacksEqual(oldStack, newStack);
    }

    /**
     * Called when the player is mining a block and the item in his hand changes.
     * Allows to not reset blockbreaking if only NBT or similar changes.
     *
     * @param oldStack The old stack that was used for mining. Item in players main
     *                 hand
     * @param newStack The new stack
     * @return True to reset block break progress
     */
    default boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack)
    {
        return !(newStack.getItem() == oldStack.getItem() && ItemStack.areItemStackTagsEqual(newStack, oldStack)
                && (newStack.isItemStackDamageable() || newStack.getItemDamage() == oldStack.getItemDamage()));
    }

    /**
     * Called to get the Mod ID of the mod that *created* the ItemStack, instead of
     * the real Mod ID that *registered* it.
     *
     * For example the Forge Universal Bucket creates a subitem for each modded
     * fluid, and it returns the modded fluid's Mod ID here.
     *
     * Mods that register subitems for other mods can override this. Informational
     * mods can call it to show the mod that created the item.
     *
     * @param itemStack the ItemStack to check
     * @return the Mod ID for the ItemStack, or null when there is no specially
     *         associated mod and {@link #getRegistryName()} would return null.
     */
    @Nullable
    default String getCreatorModId(ItemStack itemStack)
    {
        return net.minecraftforge.common.ForgeHooks.getDefaultCreatorModId(itemStack);
    }

    /**
     * Called from ItemStack.setItem, will hold extra data for the life of this
     * ItemStack. Can be retrieved from stack.getCapabilities() The NBT can be null
     * if this is not called from readNBT or if the item the stack is changing FROM
     * is different then this item, or the previous item had no capabilities.
     *
     * This is called BEFORE the stacks item is set so you can use stack.getItem()
     * to see the OLD item. Remember that getItem CAN return null.
     *
     * @param stack The ItemStack
     * @param nbt   NBT of this item serialized, or null.
     * @return A holder instance associated with this ItemStack where you can hold
     *         capabilities for the life of this item.
     */
    @Nullable
    default net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return null;
    }

    default com.google.common.collect.ImmutableMap<String, net.minecraftforge.common.animation.ITimeValue> getAnimationParameters(final ItemStack stack,
            final World world, final EntityLivingBase entity)
    {
        com.google.common.collect.ImmutableMap.Builder<String, net.minecraftforge.common.animation.ITimeValue> builder = com.google.common.collect.ImmutableMap
                .builder();
        for (ResourceLocation location : ((RegistrySimple<ResourceLocation, IItemPropertyGetter>) getItem().properties).getKeys())
        {
            final IItemPropertyGetter parameter = getItem().properties.getObject(location);
            builder.put(location.toString(), input -> parameter.call(stack, world, entity));
        }
        return builder.build();
    }

    /**
     * Can this Item disable a shield
     *
     * @param stack    The ItemStack
     * @param shield   The shield in question
     * @param entity   The EntityLivingBase holding the shield
     * @param attacker The EntityLivingBase holding the ItemStack
     * @retrun True if this ItemStack can disable the shield in question.
     */
    default boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker)
    {
        return this instanceof ItemAxe;
    }

    /**
     * Is this Item a shield
     *
     * @param stack  The ItemStack
     * @param entity The Entity holding the ItemStack
     * @return True if the ItemStack is considered a shield
     */
    default boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity)
    {
        return stack.getItem() == Items.SHIELD;
    }

    /**
     * @return the fuel burn time for this itemStack in a furnace. Return 0 to make
     *         it not act as a fuel. Return -1 to let the default vanilla logic
     *         decide.
     */
    default int getBurnTime(ItemStack itemStack)
    {
        return -1;
    }

    /**
     * Returns an enum constant of type {@code HorseArmorType}. The returned enum
     * constant will be used to determine the armor value and texture of this item
     * when equipped.
     *
     * @param stack the armor stack
     * @return an enum constant of type {@code HorseArmorType}. Return
     *         HorseArmorType.NONE if this is not horse armor
     */
    default HorseArmorType getHorseArmorType(ItemStack stack)
    {
        return HorseArmorType.getByItem(stack.getItem());
    }

    default String getHorseArmorTexture(EntityLiving wearer, ItemStack stack)
    {
        return getHorseArmorType(stack).getTextureName();
    }

    /**
     * Called every tick from {@link EntityHorse#onUpdate()} on the item in the
     * armor slot.
     *
     * @param world the world the horse is in
     * @param horse the horse wearing this armor
     * @param armor the armor itemstack
     */
    default void onHorseArmorTick(World world, EntityLiving horse, ItemStack armor)
    {
    }

    /**
     * @return This Item's renderer, or the default instance if it does not have
     *         one.
     */
    @OnlyIn(Dist.CLIENT)
    net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer getTileEntityItemStackRenderer();
}