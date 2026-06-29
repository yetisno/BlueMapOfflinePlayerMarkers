import com.technicjelle.bluemapofflineplayermarkers.core.Singletons;
import com.technicjelle.bluemapofflineplayermarkers.core.fileloader.FileMarkerLoader;
import mockery.ConsoleLogger;
import mockery.MockConfig;
import mockery.MockMarkerHandler;
import mockery.MockServer;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoadOfflineMarkersTest {
	@After
	public void cleanup() {
		if (Singletons.getServer() != null) Singletons.getServer().shutDown();
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

	@Test
	public void paper_server_uses_spigot_last_played_api() throws IOException {
		String source = Files.readString(Path.of("src/main/java/com/technicjelle/bluemapofflineplayermarkers/impl/paper/PaperServer.java"));

		assertFalse(source.contains(".getLastSeen("));
		assertTrue(source.contains(".getLastPlayed("));
	}
}
