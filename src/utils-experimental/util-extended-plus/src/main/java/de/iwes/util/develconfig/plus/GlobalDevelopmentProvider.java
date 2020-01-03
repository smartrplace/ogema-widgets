package de.iwes.util.develconfig.plus;

/**Inherit from this class once in the entire framework. Set shortId to a value indicating your custom configuration
 * and set config to the configuration you want to use. Several classes inheriting from
 * {@link GlobalDevelopmentConfiguration} can exist on the system, but only one should be activated by the single
 * class extending GlobalDevelopmentProvider.
 */
public class GlobalDevelopmentProvider {
	public static final String DEFAULT_SHORT_ID = "DefaultGC";
	public static String shortId = DEFAULT_SHORT_ID;
	public static Class<? extends GlobalDevelopmentConfiguration> config = GlobalDevelopmentConfiguration.class;
	
	@SuppressWarnings("unchecked")
	public static <T extends GlobalDevelopmentConfiguration> T getConfig() {
		try {
			return (T) config.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
}
