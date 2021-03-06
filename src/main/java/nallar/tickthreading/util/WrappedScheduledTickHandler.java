package nallar.tickthreading.util;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.SingleIntervalHandler;
import cpw.mods.fml.common.TickType;
import nallar.tickthreading.Log;

import java.util.*;

public class WrappedScheduledTickHandler implements IScheduledTickHandler {
	public final EnumSet<TickType> ticks;
	private final IScheduledTickHandler scheduledTickHandler;

	@Override
	public int nextTickSpacing() {
		return scheduledTickHandler.nextTickSpacing();
	}

	@Override
	public void tickStart(final EnumSet<TickType> type, final Object... tickData) {
		scheduledTickHandler.tickStart(type, tickData);
	}

	@Override
	public void tickEnd(final EnumSet<TickType> type, final Object... tickData) {
		scheduledTickHandler.tickEnd(type, tickData);
	}

	@Override
	public EnumSet<TickType> ticks() {
		// Making some big assumptions here - so far hasn't been wrong though!
		return ticks == null ? scheduledTickHandler.ticks() : ticks;
	}

	@Override
	public String getLabel() {
		return scheduledTickHandler.getLabel();
	}

	public WrappedScheduledTickHandler(IScheduledTickHandler scheduledTickHandler) {
		EnumSet<TickType> ticks = scheduledTickHandler.ticks();
		ITickHandler tickHandler = scheduledTickHandler;
		if (tickHandler instanceof SingleIntervalHandler) {
			tickHandler = ReflectUtil.get(scheduledTickHandler, "wrapped");
		}
		if (ticks == null || ticks.isEmpty()) {
			ticks = null;
			Log.warning("Null ticks for tick handler " + Log.toString(tickHandler) + ':' + tickHandler.getLabel());
		}
		this.ticks = ticks;
		this.scheduledTickHandler = scheduledTickHandler;
	}
}
