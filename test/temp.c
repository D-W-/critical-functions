unsigned char *ssl_add_serverhello_tlsext(SSL *s, unsigned char *p, unsigned char *limit)
unsigned char *ret = p;
ret+=2;
s2n(TLSEXT_TYPE_server_name,ret);
s2n(0,ret);
int el;
if(!ssl_add_serverhello_renegotiate_ext(s, 0, &el, 0))
s2n(TLSEXT_TYPE_renegotiate,ret);
s2n(el,ret);
if(!ssl_add_serverhello_renegotiate_ext(s, ret, &el, el))
ret += el;
s2n(TLSEXT_TYPE_ec_point_formats,ret);
s2n(s->tlsext_ecpointformatlist_length + 1,ret);
*(ret++) = (unsigned char) s->tlsext_ecpointformatlist_length;
memcpy(ret, s->tlsext_ecpointformatlist, s->tlsext_ecpointformatlist_length);
ret+=s->tlsext_ecpointformatlist_length;
s2n(TLSEXT_TYPE_session_ticket,ret);
s2n(0,ret);
s2n(TLSEXT_TYPE_status_request,ret);
s2n(0,ret);
size_t sol = s->s3->server_opaque_prf_input_len;
s2n(TLSEXT_TYPE_opaque_prf_input, ret);
s2n(sol + 2, ret);
s2n(sol, ret);
memcpy(ret, s->s3->server_opaque_prf_input, sol);
ret += sol;
int el;
ssl_add_serverhello_use_srtp_ext(s, 0, &el, 0);
s2n(TLSEXT_TYPE_use_srtp,ret);
s2n(el,ret);
if(ssl_add_serverhello_use_srtp_ext(s, ret, &el, el))
ret+=el;
memcpy(ret,cryptopro_ext,36);
