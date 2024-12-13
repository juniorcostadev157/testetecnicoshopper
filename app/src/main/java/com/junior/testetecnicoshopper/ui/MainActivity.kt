package com.junior.testetecnicoshopper.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.junior.testetecnicoshopper.R
import com.junior.testetecnicoshopper.databinding.ActivityMainBinding
import com.junior.testetecnicoshopper.ui.fragments.HistoricoFragment
import com.junior.testetecnicoshopper.ui.fragments.OptionMotoristaFragment
import com.junior.testetecnicoshopper.ui.fragments.SolicitacaoFragment

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar TabLayout
        tabLayout = findViewById(binding.tabLayout.id)

        // Configurar as abas
        tabLayout.addTab(tabLayout.newTab().setText("Solicitação"))
        tabLayout.addTab(tabLayout.newTab().setText("Motoristas"))
        tabLayout.addTab(tabLayout.newTab().setText("Histórico"))

        // Tornar o TabLayout completamente não interativo
        disableTabClicks()

        // Monitorar mudanças de fragmento para atualizar o TabLayout
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(binding.fragmentContainer.id)
            when (currentFragment) {
                is SolicitacaoFragment -> setTabIndicator(0)
                is OptionMotoristaFragment -> setTabIndicator(1)
                is HistoricoFragment -> setTabIndicator(2)
            }
        }

        // Abrir a tela inicial
        if (savedInstanceState == null) {
            openFragment(SolicitacaoFragment())
            setTabIndicator(0)
        }
    }


    private fun disableTabClicks() {
        for (i in 0 until tabLayout.tabCount) {
            val tab = (tabLayout.getTabAt(i)?.view)
            tab?.isEnabled = false
        }
    }


    private fun setTabIndicator(position: Int) {
        tabLayout.selectTab(tabLayout.getTabAt(position))
    }


    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        // Atualizar o TabLayout com base no tipo de fragmento
        when (fragment) {
            is SolicitacaoFragment -> setTabIndicator(0)
            is OptionMotoristaFragment -> setTabIndicator(1)
            is HistoricoFragment -> setTabIndicator(2)
        }
    }
}
