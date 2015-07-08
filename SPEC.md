# Signed redirect

When a signed redirect is done, the following query parameters are included in
the URL:

- `first-name`
- `last-name`
- `birth-date`
- `email`
- `should-pay`
- `has-paid`
- `signature`

The parameters `first-name`, `last-name` and `email` can have arbitrary URL
encoded UTF-8 values. The value of the parameter `birth-date` is an UTF-8
string representing a date in the ISO format `YYYY-MM-DD`. The `should-pay`
and `has-paid` parameters take only the UTF-8 string values `true` and
`false`.

The value of the `signature` parameter is an URL encoded base64 encoded
RSA-SHA256 signature of the parameter values. The values are first
concatenated in the order of the list above. The UTF-8 bytes of the formed
string are then signed with a private RSA key. The signature is then base64
encoded and finally URL encoded.

## Examples

http://www.example.com/?birth-date=1990-12-31&email=teppo.testaaja%2540example.com&signature=Wszo4tVdnFLIiiSLwz5rtwHqyEQzypZ9sGbBDVPzmIHAVzr43Wu7BFO6kWhv8o%252FqJv9OlqKq0%252Fr8bm3oqdE8iA%253D%253D&should-pay=true&first-name=Teppo&has-paid=false&last-name=Testaaja

Here the signature was calculated for the string
`TeppoTestaaja1990-12-21teppo.testaaja@example.comtruefalse`. Note that the
parameter values are URL encoded *after* the signing.
