package ru.vsu.cs.utils;

import lombok.RequiredArgsConstructor;
import org.hashids.Hashids;
import org.springframework.stereotype.Component;

/**
 * Utility for encrypting and decrypting the id in the link
 */
@RequiredArgsConstructor
@Component
public class Decoder {

    private final Hashids hashids;

    public Long idOf(String value) {
        long[] res = hashids.decode(value);
        if (res != null && res.length > 0) {
            return res[0];
        }
        return null;
    }
}
