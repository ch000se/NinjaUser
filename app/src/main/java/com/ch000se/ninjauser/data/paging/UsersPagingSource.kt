package com.ch000se.ninjauser.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ch000se.ninjauser.data.local.UserDao
import com.ch000se.ninjauser.data.mapper.toDbModelList
import com.ch000se.ninjauser.data.mapper.toUserListFromDto
import com.ch000se.ninjauser.data.remote.NinjaApiService
import com.ch000se.ninjauser.domain.User

class UsersPagingSource(
    private val api: NinjaApiService,
    private val dao: UserDao,
    private val onUsersLoaded: (List<User>) -> Unit
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val usersDto = api.getUsers(30)
            val users = usersDto.toUserListFromDto()

            onUsersLoaded(users)
            dao.replaceAllUsers(usersDto.toDbModelList())

            LoadResult.Page(
                data = users,
                prevKey = null,
                nextKey = (params.key ?: 0) + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition
    }

}