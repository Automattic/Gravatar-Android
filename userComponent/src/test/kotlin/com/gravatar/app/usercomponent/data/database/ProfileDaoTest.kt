package com.gravatar.app.usercomponent.data.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gravatar.app.usercomponent.data.database.model.ProfileEntity
import com.gravatar.app.usercomponent.data.database.model.ProfileWithVerifiedAccounts
import com.gravatar.app.usercomponent.data.database.model.VerifiedAccountEntity
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
    fun insertAndGetFullProfile() = runTest {
        // Given
        val profileWithVerifiedAccounts = createTestProfile()

        // When
        profileDao.insertProfileWithVerifiedAccounts(
            profileWithVerifiedAccounts.profile,
            profileWithVerifiedAccounts.verifiedAccounts
        )
        val retrievedProfile = profileDao.getProfileWithVerifiedAccounts().firstOrNull()

        // Then
        assertEquals(profileWithVerifiedAccounts, retrievedProfile)
    }

    @Test
    fun getProfileWhenEmpty() = runTest {
        // When
        val retrievedProfile = profileDao.getProfileWithVerifiedAccounts().firstOrNull()

        // Then
        assertNull(retrievedProfile)
    }

    @Test
    fun insertReplaceAndGetProfile() = runTest {
        // Given
        val profile1 = createTestProfile(userId = 123, displayName = "Original Name")
        val profile2 = createTestProfile(userId = 123, displayName = "Updated Name")

        // When
        profileDao.insertProfileWithVerifiedAccounts(profile1.profile, profile1.verifiedAccounts)
        profileDao.insertProfileWithVerifiedAccounts(profile2.profile, profile2.verifiedAccounts)
        val retrievedProfile = profileDao.getProfileWithVerifiedAccounts().firstOrNull()

        // Then
        assertEquals(profile2, retrievedProfile)
    }

    @Test
    fun deleteProfile() = runTest {
        // Given
        val profileWithVerifiedAccounts = createTestProfile()
        profileDao.insertProfileWithVerifiedAccounts(
            profileWithVerifiedAccounts.profile,
            profileWithVerifiedAccounts.verifiedAccounts
        )

        // Verify profile was inserted
        val profileBeforeDelete = profileDao.getProfileWithVerifiedAccounts().firstOrNull()
        assertEquals(profileWithVerifiedAccounts, profileBeforeDelete)

        // When
        profileDao.delete()

        // Then
        val profileAfterDelete = profileDao.getProfileWithVerifiedAccounts().firstOrNull()
        assertNull(profileAfterDelete)
    }

    private fun createTestProfile(
        userId: Int = 123,
        displayName: String = "Test User"
    ): ProfileWithVerifiedAccounts {
        return ProfileWithVerifiedAccounts(
            profile = ProfileEntity(
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
            ),
            verifiedAccounts = listOf(createTestVerifiedAccountEntity())
        )
    }

    private fun createTestVerifiedAccountEntity(id: Long = 1): VerifiedAccountEntity {
        return VerifiedAccountEntity(
            id = id,
            profileUserId = 123,
            serviceLabel = "TestService",
            serviceType = "type",
            serviceIcon = "https://example.com/icon.png",
            url = "https://example.com/testuser",
            isHidden = false
        )
    }
}
