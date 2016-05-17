package mcjty.theoneprobe.apiimpl;

import cofh.api.energy.IEnergyHandler;
import mcjty.theoneprobe.Config;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.elements.ElementProgress;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class DefaultProbeInfoProvider implements IProbeInfoProvider {

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, World world, IBlockState blockState, BlockPos pos) {
        Block block = blockState.getBlock();
        String modid = getModName(block);

        Item item = Item.getItemFromBlock(block);
        if (item != null) {
            ItemStack stack = new ItemStack(item, 1);
            probeInfo.horizontal()
                    .item(stack)
                    .vertical()
                        .text(TextFormatting.WHITE + stack.getDisplayName())
                        .text(TextFormatting.BLUE + modid);
        } else {
            probeInfo.horizontal()
                    .text(TextFormatting.WHITE + block.getLocalizedName())
                    .text(TextFormatting.BLUE + modid);
        }

        if (mode == ProbeMode.EXTENDED) {
            if (Config.showHarvestLevel) {
                String harvestTool = block.getHarvestTool(blockState);
                if (harvestTool != null) {
                    probeInfo.text(TextFormatting.GREEN + "Harvest level: " + harvestTool);
                }
            }
        }

        if (mode == ProbeMode.DEBUG && Config.showDebugInfo) {
            IProbeInfo vertical = probeInfo.vertical(0xffff4444)
                    .text("Unlocname: " + block.getUnlocalizedName())
                    .text("Meta: " + blockState.getBlock().getMetaFromState(blockState));
            TileEntity te = world.getTileEntity(pos);
            if (te != null) {
                vertical.text("TE: " + te.getClass().getSimpleName());
            }
        }

        if (Config.showRF > 0) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof IEnergyHandler) {
                IEnergyHandler handler = (IEnergyHandler) te;
                int energy = handler.getEnergyStored(EnumFacing.DOWN);
                int maxEnergy = handler.getMaxEnergyStored(EnumFacing.DOWN);
                if (Config.showRF == 1) {
                    probeInfo.progress(energy, maxEnergy, "", "RF",
                            new ProgressStyle().filledColor(0xffdd0000).alternateFilledColor(0xff430000).borderColor(0xff555555).numberFormat(Config.rfFormat));
                } else {
                    probeInfo.text(TextFormatting.GREEN + "RF: " + ElementProgress.format(energy, Config.rfFormat) + "RF");
                }
            }
        }
    }

    private String getModName(Block block) {
        String modid = block.getRegistryName().getResourceDomain();
        for (ModContainer container : Loader.instance().getActiveModList()) {
            if (modid.equals(container.getModId())) {
                modid = container.getName();
                break;
            }
        }
        return modid;
    }
}
