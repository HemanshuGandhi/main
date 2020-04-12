package com.notably.model;

import com.notably.model.block.BlockModel;
import com.notably.model.suggestion.SuggestionModel;
import com.notably.model.userpref.UserPrefModel;
import com.notably.model.viewstate.ViewStateModel;

/**
 * The API of the {@link Model} component.
 */
public interface Model extends BlockModel, SuggestionModel, UserPrefModel, ViewStateModel {
}

