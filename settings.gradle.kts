pluginManagement {
	repositories {
		google()				// plugin Android dari Google
		mavenCentral()			// library umum
		gradlePluginPortal()
	}
}
dependencyResolutionManagement {
	// semua repository diatur disini, modul tidak boleh menambah sendiri
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
	}
}
rootProject.name = "KasPribadi"
include(":app")		// daftarkan folder app sebagai modul
