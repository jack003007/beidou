package com.ty.beidou.presenter;

import com.ty.beidou.view.ILoginView;

/**
 * Created by ty on 2016/9/12.
 */
public class RegisPresenterCompl implements IRegisPresenter {

    private ILoginView loginView;

    public RegisPresenterCompl(ILoginView loginView) {
        this.loginView = loginView;
    }


    @Override
    public void doRegis() {
        loginView.onRegisResult();
    }


}
