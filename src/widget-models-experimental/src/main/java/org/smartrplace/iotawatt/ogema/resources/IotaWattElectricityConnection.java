package org.smartrplace.iotawatt.ogema.resources;

import java.time.Duration;
import java.util.Optional;

import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.connections.ElectricityConnection;
import org.ogema.model.prototypes.PhysicalElement;

/** Note that there are two configuration options for Iotwatt. For a simpler configuration
 * 		just writing values into the destination resources see {@link IotaWattConnection}.<br> 
 * This configuration writes directly into the slotsDB of the destination resources.
 * All data measured by Iotawatt is recorded
 *
 * @author jlapp
 */
public interface IotaWattElectricityConnection extends PhysicalElement {
    
    /**
     * @return URI of the IotaWatt query API.
     */    
    StringResource uri();
    
    /**
     * Time window to use for average power readings as ISO8601 duration string.
     * Default is {@code PT15m}, i.e.  15 minutes.<br>
     * Should usually be equal or shorter than {@link #updateInterval()} and {@link #updateIntervalEnergy()}.
     * 
     * @return Time window to use when computing average power readings. 
     */
    StringResource readingInterval();
    
    default Optional<Duration> getReadingInterval() {
        return readingInterval().isActive() 
                ? Optional.of(Duration.parse(readingInterval().getValue()))
                : Optional.empty();
    }
    
    /**
     * Update interval for IotaWatt power readings as ISO8601 duration string.
     * Default is {@code PT15m}, i.e.  15 minutes.
     * 
     * @return Interval at which to update readings.
     */
    StringResource updateInterval();
    
    default Optional<Duration> getUpdateInterval() {
        return updateInterval().isActive() 
                ? Optional.of(Duration.parse(updateInterval().getValue()))
                : Optional.empty();
    }
    
    /**
     * Update interval for IotaWatt energy readings as ISO8601 duration string.
     * Default is {@code PT15m}, i.e.  15 minutes.
     * 
     * @return Interval at which to update readings.
     */
    StringResource updateIntervalEnergy();
    
    default Optional<Duration> getUpdateIntervalEnergy() {
        return updateIntervalEnergy().isActive() 
                ? Optional.of(Duration.parse(updateIntervalEnergy().getValue()))
                : Optional.empty();
    }
    
    /**
     * Set to true if the total energy should not be requested from the IotaWatt.
     * 
     * @return Do not request total energy reading?
     */
    BooleanResource disableEnergyReading();
    
    default boolean isEnergyReadingDisabled() {
        return disableEnergyReading().isActive() ? disableEnergyReading().getValue() : false;
    }
    
    /**
     * @return (optional) Name of the IotaWatt voltage series.
     */
    StringResource voltage();
    
    /**
     * If the electricity connection has only one phase, set the name of the
     * IotaWatt power series in this resource. The {@code subPhaseConnections}
     * element of {@link #elConn} will not be used.
     * 
     * @return Name of the IotaWatt power series for a single phase connection.
     */
    StringResource singlePhasePower();

    /**
     * A list of {@code <phase name>:<series name>} pairs that define how
     * to map IotaWatt power series readings onto sub phases in {@link #elConn}.
     * 
     * @return List of the IotaWatt power series for the ElecticityConnection's phases.
     */
    StringArrayResource phases();
    
    /**
     * The {@link ElectricityConnection} that will receive the IotaWatt readings.
     * If {@link #phases() } is used, data will be written into the respective
     * {@link ElectricityConnection#subPhaseConnections() } elements.
     * 
     * @return The target ElectricityConnection, usually set as reference.
     */
    ElectricityConnection elConn();
    
}
