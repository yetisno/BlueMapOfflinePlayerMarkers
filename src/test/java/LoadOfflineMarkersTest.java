import com.technicjelle.bluemapofflineplayermarkers.core.Singletons;
import com.technicjelle.bluemapofflineplayermarkers.core.fileloader.FileMarkerLoader;
import mockery.ConsoleLogger;
import mockery.MockConfig;
import mockery.MockMarkerHandler;
import mockery.MockServer;
import org.junit.After;
import org.junit.Test;

public class LoadOfflineMarkersTest {
	@After
	public void cleanup() {
		Singletons.getServer().shutDown();
		Singletons.cleanup();
	}

	@Test
	public void extract_info_from_playerdata_files() {
		Singletons.init(
				new MockServer("test_playerdata"),
				ConsoleLogger.createLogger("extract_info_from_playerdata_files"),
				new MockConfig(),
				new MockMarkerHandler()
		);
		Singletons.getServer().startUp();
		FileMarkerLoader.loadOfflineMarkers(null);
	}
}
