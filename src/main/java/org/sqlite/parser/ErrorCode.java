package org.sqlite.parser;

enum ErrorCode {
    UnrecognizedToken,
    UnterminatedLiteral,
    UnterminatedBracket,
    UnterminatedBlockComment,
    BadVariableName,
    BadNumber,
    ExpectedEqualsSign,
    MalformedBlobLiteral,
    MalformedHexInteger,
    TokenTooLong,
    ScanError,
}