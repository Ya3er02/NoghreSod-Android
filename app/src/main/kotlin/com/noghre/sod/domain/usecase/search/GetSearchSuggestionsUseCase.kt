package com.noghre.sod.domain.usecase.search

import com.noghre.sod.domain.base.UseCase
import com.noghre.sod.domain.repository.SearchRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class GetSearchSuggestionsUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) : UseCase<GetSearchSuggestionsUseCase.Params, List<String>>() {

    data class Params(val query: String)

    override suspend fun execute(params: Params): List<String> {
        if (params.query.isEmpty()) {
            return emptyList()
        }
        return searchRepository.getSearchSuggestions(params.query)
    }
}
