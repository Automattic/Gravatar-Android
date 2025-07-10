package com.gravatar.app.usercomponent.data.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gravatar.app.usercomponent.data.database.model.ProfileEntity
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class ProfileDaoTest {

    private lateinit var userDatabase: UserDatabase
    private lateinit var profileDao: ProfileDao

    @Before
    fun setup() {
        // Create an in-memory database
        userDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            UserDatabase::class.java
        ).allowMainThreadQueries().build()

        profileDao = userDatabase.profileDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        userDatabase.close()
    }

    @Test
    fun insertAndGetProfile() = runTest {
        // Given
        val profileEntity = createTestProfileEntity()

        // When
        profileDao.insertProfile(profileEntity)
        val retrievedProfile = profileDao.getProfile().firstOrNull()

        // Then
        assertEquals(profileEntity, retrievedProfile)
    }

    @Test
    fun getProfileWhenEmpty() = runTest {
        // When
        val retrievedProfile = profileDao.getProfile().firstOrNull()

        // Then
        assertNull(retrievedProfile)
    }

    @Test
    fun insertReplaceAndGetProfile() = runTest {
        // Given
        val profileEntity1 = createTestProfileEntity(userId = 123, displayName = "Original Name")
        val profileEntity2 = createTestProfileEntity(userId = 123, displayName = "Updated Name")

        // When
        profileDao.insertProfile(profileEntity1)
        profileDao.insertProfile(profileEntity2)
        val retrievedProfile = profileDao.getProfile().firstOrNull()

        // Then
        assertEquals(profileEntity2, retrievedProfile)
    }

    @Test
    fun deleteProfile() = runTest {
        // Given
        val profileEntity = createTestProfileEntity()
        profileDao.insertProfile(profileEntity)

        // Verify profile was inserted
        val profileBeforeDelete = profileDao.getProfile().firstOrNull()
        assertEquals(profileEntity, profileBeforeDelete)

        // When
        profileDao.delete()

        // Then
        val profileAfterDelete = profileDao.getProfile().firstOrNull()
        assertNull(profileAfterDelete)
    }

    private fun createTestProfileEntity(
        userId: Int = 123,
        displayName: String = "Test User"
    ): ProfileEntity {
        return ProfileEntity(
            userId = userId,
            hash = "test-hash",
            displayName = displayName,
            profileUrl = "https://example.com/profile",
            avatarUrl = "https://example.com/avatar",
            avatarAltText = "",
            description = "Test Description",
            pronouns = "they/them",
            pronunciation = "Test Pronunciation",
            location = "Test Location",
            jobTitle = "Test Job",
            company = "Test Company",
            firstName = "Test",
            lastName = "User",
            contactCellPhone = "123-456-7890",
            contactEmail = "test@example.com"
        )
    }
}
