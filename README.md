# Teste Técnico Shopper

Este projeto é uma aplicação Android desenvolvida como parte de um teste técnico para a Shopper, utilizando tecnologias como Retrofit, Google Maps API e Android Jetpack.

## Configuração Inicial

### Requisitos
- **Android Studio**: Koala Feature Drop 2024.1.2
- **Gradle Wrapper**: 8.2 ou superior
- **Android Gradle Plugin**: 8.1.2 ou superior



### Clonando o Projeto
Clone o repositório e importe-o no Android Studio.

```bash
git clone <URL_DO_REPOSITORIO>
```

### Configuração do Google Maps API Key
A chave da API do Google Maps deve ser configurada para permitir a utilização de recursos como mapas estáticos e rotas.
Por padrão, o arquivo `AndroidManifest.xml` contém o local onde a chave deve ser inserida:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="" />
```

Por favor, substitua `""` pela sua chave de API durante o desenvolvimento. **Nos testes, utilizaremos nossa própria chave de API.** Certifique-se de deixar essa informação clara no `README.md`.

---

## Estrutura do Projeto
O projeto segue o padrão MVC com os seguintes diretórios principais:

- **app**: Contém o código-fonte principal, incluindo a interface do usuário (UI), os modelos de dados e os controladores.
- **gradle**: Contém os arquivos de configuração do Gradle.
- **libs**: Contém as dependências externas.

---

## Configuração do Gradle

### Arquivo `build.gradle.kts` (Módulo `app`)
Certifique-se de que o arquivo `build.gradle.kts` está configurado conforme abaixo:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "com.junior.testetecnicoshopper"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.junior.testetecnicoshopper"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true
    }
}
```

Se houver problemas com `libs.plugins`, substitua os aliases pelos respectivos IDs diretos, conforme o exemplo abaixo:

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}
```

### Dependências Principais
As seguintes dependências são utilizadas neste projeto:

- **Lifecycle Components:**
  ```kotlin
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
  ```

- **Retrofit (para chamadas de API):**
  ```kotlin
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-gson:2.9.0")
  ```

- **Glide (para carregamento de imagens):**
  ```kotlin
  implementation("com.github.bumptech.glide:glide:4.15.1")
  ```

- **Google Maps Services:**
  ```kotlin
  implementation("com.google.android.gms:play-services-maps:19.0.0")
  ```

---

## Problemas Comuns e Soluções

1. **Erro de Compatibilidade do Gradle:** Atualize o `Gradle Wrapper` para a versão recomendada no arquivo `gradle/wrapper/gradle-wrapper.properties`:
   ```properties
   distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
   ```

2. **Erro com Plugins `libs.plugins`:** Verifique se o arquivo `libs.versions.toml` está corretamente configurado na pasta `gradle` do projeto.

3. **Chave de API ausente ou incorreta:** Certifique-se de configurar corretamente no `AndroidManifest.xml`. Durante os testes, utilizaremos nossa própria chave de API.

---

## Licença
Este projeto foi desenvolvido exclusivamente para fins de avaliação técnica pela Shopper. O código está sujeito às diretrizes fornecidas pela empresa.

---

Em caso de dúvidas ou problemas, entre em contato com o desenvolvedor responsável.

