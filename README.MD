# Translation Fallbacks
A library that allows translations to have multiple fallbacks (primarily for Emotecraft)

## Usage
Define translation keys with a default fallback and optional language-specific fallbacks:

```json
{
  "translate": "translation.key",
  "fallback": "default", // vanilla behavior (fallback for all)
  "fallbacks": { // Separated into its own field to avoid breaking compatibility with vanilla
    "ru_ru": "дефолт" // fallback for ru_ru
  }
}
```
For detailed examples and test cases, refer to the [unit tests](https://github.com/dima-dencep/TranslationFallbacks/blob/main/fabric/src/test/java/tests/FallbacksTest.java).

## Adventure
This library also supports Adventure! To use it, apply the patcher from the [Adventure subproject](https://github.com/dima-dencep/TranslationFallbacks/tree/main/adventure)!
But keep in mind that Adventure does nothing with fallbacks, so you'll have to implement that yourself.
