package dev.faststats.bukkit;

import com.destroystokyo.paper.event.server.ServerExceptionEvent;
import com.destroystokyo.paper.exception.ServerException;
import com.destroystokyo.paper.exception.ServerPluginException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

final class PaperEventListener implements Listener {
    private final BukkitMetricsImpl metrics;

    PaperEventListener(BukkitMetricsImpl metrics) {
        this.metrics = metrics;
    }

    public BukkitMetricsImpl metrics() {
        return metrics;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerException(final ServerExceptionEvent event) {
        final ServerException serverException = event.getException();
        if (!(serverException instanceof ServerPluginException)) return;
        final ServerPluginException exception = (ServerPluginException) serverException;
        if (!exception.getResponsiblePlugin().equals(metrics.plugin())) return;
        final Throwable report = exception.getCause() != null ? exception.getCause() : exception;
        metrics.getErrorTracker().ifPresent(tracker -> tracker.trackError(report, false));
    }
}
